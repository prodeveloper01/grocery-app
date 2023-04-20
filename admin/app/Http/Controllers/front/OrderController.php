<?php

namespace App\Http\Controllers\front;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\DB;
use App\OrderDetails;
use App\Cart;
use App\About;
use App\Category;
use App\User;
use App\Pincode;
use App\Order;
use App\Payment;
use App\Transaction;
use Session;
use Storage;
use Validator;
use Illuminate\Support\Facades\Redirect;
use Auth;
use Stripe\Stripe;
use Stripe\Customer;
use Stripe\Charge;
use Razorpay\Api\Api;

class OrderController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index() {
        $user_id  = @Auth::user()->id;

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $orderdata=OrderDetails::select('order.order_total as total_price',DB::raw('SUM(order_details.qty) AS qty'),'order.id',DB::raw('DATE_FORMAT(order.created_at, "%d %M %Y") as date'),'order.order_number','order.order_type','order.status','order.payment_type')
        ->join('item','order_details.item_id','=','item.id')
        ->join('order','order_details.order_id','=','order.id')
        ->where('order.user_id',@Auth::user()->id)->groupBy('order_details.order_id')->orderby('order.id','desc')->paginate(9);

        $getabout = About::where('id','=','1')->first();

        if (Auth::guest()) {
            return view('front.login', compact('cart','getdata','getcategory','getabout'));
        } else {
            return view('front.orders',compact('orderdata','getcategory','cart','getdata','getabout'));
        }
    }

    public function orderdetails(Request $request) {
        
        @$user_id  = Auth::user()->id;

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();


        $orderdata=OrderDetails::with('itemimage')->select('order_details.id','order_details.qty','order_details.price as total_price','item.id','item.item_name','item.slug','order_details.item_id','order_details.item_notes','order_details.weight')
        ->join('item','order_details.item_id','=','item.id')
        ->join('order','order_details.order_id','=','order.id')
        ->where('order_details.order_id',$request->id)->get();

        if(count($orderdata) == 0){ 
            abort(404); 
        } else {
            
            $status=Order::select('order.driver_id','order.order_number','order.order_total',DB::raw('DATE_FORMAT(order.created_at, "%d %M %Y") as date'),'order.address','order.payment_type','order.building','order.landmark','order.pincode','order.order_type','order.promocode','order.id','order.discount_amount','order.order_number','order.status','order.order_notes','order.tax','order.tax_amount','order.delivery_charge')->where('order.id',$request->id)
            ->get()->first();

            $getdriver=User::select('users.name',\DB::raw("CONCAT('".url('/public/images/profile/')."/', users.profile_image) AS profile_image"),'users.mobile')->where('users.id',$status->driver_id)
            ->get()->first();

            if (@$getdriver["name"] == "") {
                $drivername = "";
                $driverprofile_image = "";
                $drivermobile = "";
            } else {
                $drivername = $getdriver["name"];
                $driverprofile_image = $getdriver["profile_image"];
                $drivermobile = $getdriver["mobile"];
            }

            $summery = array(
                'id' => $status->id,
                'tax' => $status->tax,
                'tax_amount' => $status->tax_amount,
                'order_total' => $status->order_total,
                'discount_amount' => $status->discount_amount,
                'order_number' => $status->order_number,
                'created_at' => $status->date,
                'promocode' => $status->promocode,
                'payment_type' => $status->payment_type,
                'delivery_charge' => $status->delivery_charge,
                'address' => $status->address,
                'building' => $status->building,
                'landmark' => $status->landmark,
                'pincode' => $status->pincode,
                'order_notes' => $status->order_notes,
                'status' => $status->status,
                'order_type' => $status->order_type,
                'driver_name' => $drivername,
                'driver_profile_image' => $driverprofile_image,
                'driver_mobile' => $drivermobile,
            );
        }
        $getabout = About::where('id','=','1')->first();

        return view('front.order-details',compact('orderdata','summery','getcategory','cart','getdata','getabout'));
    }

    public function track(Request $request) {
        @$user_id  = Auth::user()->id;

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $orderdata=OrderDetails::with('itemimage')->select('order_details.id','order_details.qty','order_details.price as total_price','item.id','item.item_name','item.slug','order_details.item_id','order_details.item_notes','order_details.weight')
        ->join('item','order_details.item_id','=','item.id')
        ->join('order','order_details.order_id','=','order.id')
        ->where('order_details.order_id',$request->id)->get();

        $status=Order::select('order_number',DB::raw('DATE_FORMAT(created_at, "%d %M %Y") as date'),'address','status','tax','tax_amount','order_type')->where('order.id',$request->id)
        ->get()->first();

        $getabout = About::where('id','=','1')->first();

        return view('front.track-order', compact('orderdata','status','getcategory','cart','getdata','getabout'));
    }

    public function cashondelivery(Request $request)
    {
        if ($request->order_type == "1") {
            if($request->address == ""){
                return response()->json(["status"=>0,"message"=>"Address is required"],200);
            }

            if($request->lat == ""){
                return response()->json(["status"=>0,"message"=>"Please select the address from suggestion"],200);
            }

            if($request->lang == ""){
                return response()->json(["status"=>0,"message"=>"Please select the address from suggestion"],200);
            }

            if($request->pincode == ""){
                return response()->json(["status"=>0,"message"=>"Pincode is required"],200);
            }

            if($request->building == ""){
                return response()->json(["status"=>0,"message"=>"Door / Flat No. is required"],200);
            }

            if($request->landmark == ""){
                return response()->json(["status"=>0,"message"=>"Landmark is required"],200);
            }
        } 

        $getuserdata=User::where('id',Auth::user()->id)->first();

        $getdata=User::select('min_order_amount','max_order_amount','currency')->where('type','1')->first();

        if ($request->discount_amount == "NaN") {
            $discount_amount = "0.00";
        } else {
            $discount_amount = $request->discount_amount;
        }     

        try {

            $order_number = substr(str_shuffle(str_repeat("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", 10)), 0, 10);

            if ($request->order_type == "1") {
                $delivery_charge = $request->delivery_charge;
                $address = $request->address;
                $lat = $request->lat;
                $lang = $request->lang;
                $total_amount = $request->total_amount;
                $building = $request->building;
                $landmark = $request->landmark;
                $pincode = $request->pincode;
            } else {
                $delivery_charge = "0.00";
                $address = "";
                $lat = "";
                $lang = "";
                $building = "";
                $landmark = "";
                $pincode = "";
                $total_amount = $request->total_amount;                
            }

            if ($getdata->min_order_amount > $request->subtotal) {
                return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
            }

            if ($getdata->max_order_amount < $request->subtotal) {
                return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
            }
            $order = new Order;
            $order->order_number =$order_number;
            $order->user_id =Auth::user()->id;
            $order->order_total =$total_amount;
            $order->payment_type ='0';
            $order->status ='1';
            $order->address =$address;
            $order->promocode =$request->promocode;
            $order->discount_amount =$discount_amount;
            $order->discount_pr =$request->discount_pr;
            $order->tax =$request->tax;
            $order->tax_amount =$request->tax_amount;
            $order->delivery_charge =$delivery_charge;
            $order->order_type =$request->order_type;
            $order->ordered_date =$request->ordered_date;            
            $order->lat =$lat;
            $order->lang =$lang;
            $order->building =$building;
            $order->landmark =$landmark;
            $order->pincode =$pincode;
            $order->order_notes =$request->notes;
            $order->order_from ='web';

            $order->save();

            $order_id = DB::getPdo()->lastInsertId();
            $data=Cart::where('cart.user_id',Auth::user()->id)
            ->get();

            foreach ($data as $value) {
                $OrderPro = new OrderDetails;
                $OrderPro->order_id = $order_id;
                $OrderPro->user_id = $value['user_id'];
                $OrderPro->item_id = $value['item_id'];
                $OrderPro->weight = $value['weight'];
                $OrderPro->item_name = $value['item_name'];
                $OrderPro->image_name = $value['image_name'];
                $OrderPro->price = $value['price'];
                $OrderPro->qty = $value['qty'];
                $OrderPro->item_notes = $value['item_notes'];
                $OrderPro->save();
            }
            $cart=Cart::where('user_id', Auth::user()->id)->delete();

            $count=Cart::where('user_id',Auth::user()->id)->count();

            try{
                $ordermessage='Order "'.$order_number.'" has been placed';
                $email=$getuserdata->email;
                $name=$getuserdata->name;
                $data=['ordermessage'=>$ordermessage,'email'=>$email,'name'=>$name];

                Mail::send('Email.orderemail',$data,function($message)use($data){
                    $message->from(env('MAIL_USERNAME'))->subject($data['ordermessage']);
                    $message->to($data['email']);
                } );
            }catch(\Swift_TransportException $e){
                $response = $e->getMessage() ;
                return response()->json(['status'=>0,'message'=>'Something went wrong while sending email Please try again...'],200);
            }

            Storage::disk('local')->put("cart", $count);

            $remove = session()->forget(['offer_amount','offer_code','order_type','address','pincode','building','landmark','notes','lat','lang','city','country','state']);
            
            return response()->json(['status'=>1,'message'=>'Order has been placed'],200);            

        } catch (\Exception $e) {
            return  $e->getMessage();
            \Session::put('error',$e->getMessage());
            return redirect()->back();
        }
    }

    public function walletorder(Request $request)
    {
        if ($request->order_type == "1") {
            if($request->address == ""){
                return response()->json(["status"=>0,"message"=>"Address is required"],200);
            }

            if($request->lat == ""){
                return response()->json(["status"=>0,"message"=>"Please select the address from suggestion"],200);
            }

            if($request->lang == ""){
                return response()->json(["status"=>0,"message"=>"Please select the address from suggestion"],200);
            }

            if($request->pincode == ""){
                return response()->json(["status"=>0,"message"=>"Pincode is required"],200);
            }

            if($request->building == ""){
                return response()->json(["status"=>0,"message"=>"Door / Flat No. is required"],200);
            }

            if($request->landmark == ""){
                return response()->json(["status"=>0,"message"=>"Landmark is required"],200);
            }
        } 

        $getuserdata=User::where('id',Auth::user()->id)->first();

        $getdata=User::select('min_order_amount','max_order_amount','currency')->where('type','1')->first();

        if ($request->discount_amount == "NaN") {
            $discount_amount = "0.00";
        } else {
            $discount_amount = $request->discount_amount;
        }     

        try {

            $order_number = substr(str_shuffle(str_repeat("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", 10)), 0, 10);

            if ($request->order_type == "1") {
                $delivery_charge = $request->delivery_charge;
                $address = $request->address;
                $lat = $request->lat;
                $lang = $request->lang;
                $total_amount = $request->total_amount;
                $building = $request->building;
                $landmark = $request->landmark;
                $pincode = $request->pincode;
            } else {
                $delivery_charge = "0.00";
                $address = "";
                $lat = "";
                $lang = "";
                $building = "";
                $landmark = "";
                $pincode = "";
                $total_amount = $request->total_amount;                
            }

            if ($getuserdata->wallet < $total_amount) {
                return response()->json(["status"=>0,"message"=>"You don't have sufficient wallet amonut. Please select another payment method"],200);
            }

            if ($getdata->min_order_amount > $request->subtotal) {
                return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
            }

            if ($getdata->max_order_amount < $request->subtotal) {
                return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
            }
            $order = new Order;
            $order->order_number =$order_number;
            $order->user_id =Auth::user()->id;
            $order->order_total =$total_amount;
            $order->payment_type ='3';
            $order->status ='1';
            $order->address =$address;
            $order->promocode =$request->promocode;
            $order->discount_amount =$discount_amount;
            $order->discount_pr =$request->discount_pr;
            $order->tax =$request->tax;
            $order->tax_amount =$request->tax_amount;
            $order->delivery_charge =$delivery_charge;
            $order->order_type =$request->order_type;
            $order->ordered_date =$request->ordered_date;
            $order->lat =$lat;
            $order->lang =$lang;
            $order->building =$building;
            $order->landmark =$landmark;
            $order->pincode =$pincode;
            $order->order_notes =$request->notes;
            $order->order_from ='web';

            $order->save();

            $order_id = DB::getPdo()->lastInsertId();
            $data=Cart::where('cart.user_id',Auth::user()->id)
            ->get();

            foreach ($data as $value) {
                $OrderPro = new OrderDetails;
                $OrderPro->order_id = $order_id;
                $OrderPro->user_id = $value['user_id'];
                $OrderPro->item_id = $value['item_id'];
                $OrderPro->weight = $value['weight'];
                $OrderPro->item_name = $value['item_name'];
                $OrderPro->image_name = $value['image_name'];
                $OrderPro->price = $value['price'];
                $OrderPro->qty = $value['qty'];
                $OrderPro->item_notes = $value['item_notes'];
                $OrderPro->save();
            }
            $cart=Cart::where('user_id', Auth::user()->id)->delete();

            $count=Cart::where('user_id',Auth::user()->id)->count();

            $wallet = $getuserdata->wallet - $total_amount;

            $UpdateWalletDetails = User::where('id', Auth::user()->id)
            ->update(['wallet' => $wallet]);

            $Wallet = new Transaction;
            $Wallet->user_id = Auth::user()->id;
            $Wallet->order_id = $order_id;
            $Wallet->order_number = $order_number;
            $Wallet->wallet = $total_amount;
            $Wallet->payment_id = NULL;
            $Wallet->order_type = $request->order_type;
            $Wallet->transaction_type = '2';
            $Wallet->save();

            try{
                $ordermessage='Order "'.$order_number.'" has been placed';
                $email=$getuserdata->email;
                $name=$getuserdata->name;
                $data=['ordermessage'=>$ordermessage,'email'=>$email,'name'=>$name];

                Mail::send('Email.orderemail',$data,function($message)use($data){
                    $message->from(env('MAIL_USERNAME'))->subject($data['ordermessage']);
                    $message->to($data['email']);
                } );
            }catch(\Swift_TransportException $e){
                $response = $e->getMessage() ;
                return response()->json(['status'=>0,'message'=>'Something went wrong while sending email Please try again...'],200);
            }

            Storage::disk('local')->put("cart", $count);

            $remove = session()->forget(['offer_amount','offer_code','order_type','address','pincode','building','landmark','notes','lat','lang','city','country','state']);
            
            return response()->json(['status'=>1,'message'=>'Order has been placed'],200);            

        } catch (\Exception $e) {
            return  $e->getMessage();
            \Session::put('error',$e->getMessage());
            return redirect()->back();
        }
    }

    public function charge(Request $request)
    {
        try {

            $getuserdata=User::where('id',Auth::user()->id)
            ->get()->first();

            $location=User::select('lat','lang','map')->where('type','1')->first();

            if ($request->order_type == "2") {
                $deal_lat=$location->lat;
                $deal_long=$location->lang;
            } else {
                $deal_lat=$request->lat;
                $deal_long=$request->lang;
            }

            if (env('Environment') == 'sendbox') {
                if ($request->order_type == "1") {
                    $delivery_charge = "0.00";
                    $address = 'New York, NY, USA';
                    $lat = '40.7127753';
                    $lang = '-74.0059728';
                    $total_amount = $request->total_amount;
                    $building = "";
                    $landmark = "";
                    $pincode = '10001';
                    $city = @$city;
                    $state = @$state;
                    $country = @$country;
                    $order_total = $request->order_total;
                } else {
                    $delivery_charge = "0.00";
                    $address = 'New York, NY, USA';
                    $lat = '40.7127753';
                    $lang = '-74.0059728';
                    $order_total = $request->order_total;
                    $building = "";
                    $landmark = "";
                    $city = @$city;
                    $state = @$state;
                    $country = @$country;
                    $pincode = '10001';
                    $total_amount = $request->subtotal;
                }
            } else{
                $gmapkey = $location->map;

                // Make the HTTP request
                $geocode=file_get_contents('https://maps.googleapis.com/maps/api/geocode/json?latlng='.$deal_lat.','.$deal_long.'&sensor=false&key='.$gmapkey.'');

                $output= json_decode($geocode);
                $formattedAddress = @$output->results[0]->formatted_address;

                for($j=0;$j<count($output->results[0]->address_components);$j++){
                    $cn=array($output->results[0]->address_components[$j]->types[0]);
                    if(in_array("country", $cn)) {
                        $country = $output->results[0]->address_components[$j]->short_name;
                    }

                    if(in_array("postal_code", $cn)) {
                        $postal_code = $output->results[0]->address_components[$j]->long_name;
                    }

                    if(in_array("administrative_area_level_2", $cn)) {
                        $city = $output->results[0]->address_components[$j]->long_name;
                    }

                    if(in_array("administrative_area_level_1", $cn)) {
                        $state = $output->results[0]->address_components[$j]->short_name;
                    }
                }

                if ($request->order_type == "1") {
                    $delivery_charge = $request->delivery_charge;
                    $address = $formattedAddress;
                    $lat = $deal_lat;
                    $lang = $deal_long;
                    $total_amount = $request->total_amount;
                    $building = $request->building;
                    $landmark = $request->landmark;
                    $pincode = $request->pincode;
                    $city = @$city;
                    $state = @$state;
                    $country = @$country;
                    $order_total = $request->order_total;
                } else {
                    $delivery_charge = "0.00";
                    $address = 'New York, NY, USA';
                    $lat = '40.7127753';
                    $lang = '-74.0059728';
                    $order_total = $request->order_total;
                    $building = $request->building;
                    $landmark = $request->landmark;
                    $city = @$city;
                    $state = @$state;
                    $country = @$country;
                    $pincode = '10001';
                    $total_amount = $request->subtotal;
                }
            }

            if ($request->discount_amount == "NaN") {
                $discount_amount = "0.00";
            } else {
                $discount_amount = $request->discount_amount;
            }

            $getpaymentdata=Payment::select('test_secret_key','live_secret_key','environment')->where('payment_name','Stripe')->first();

            if ($getpaymentdata->environment=='1') {
                $stripe_secret = $getpaymentdata->test_secret_key;
            } else {
                $$stripe_secret = $getpaymentdata->live_secret_key;
            }

            Stripe::setApiKey($stripe_secret);

            if (env('Environment') == 'sendbox') {
                $customer = Customer::create(array(
                    'email' => $request->stripeEmail,
                    'source' => $request->stripeToken,
                    'name' => $getuserdata->name,
                    'address' => [
                        'line1' => 'New York, NY, USA',
                        'postal_code' => '10001',
                        'city' => 'New York',
                        'state' => 'NY',
                        'country' => 'US',
                    ],
                ));
            } else {
                $customer = Customer::create(array(
                    'email' => $request->stripeEmail,
                    'source' => $request->stripeToken,
                    'name' => $getuserdata->name,
                    'address' => [
                        'line1' => $address,
                        'postal_code' => $pincode,
                        'city' => @$city,
                        'state' => @$state,
                        'country' => @$country,
                    ],
                ));
            }

            $charge = Charge::create(array(
                'customer' => $customer->id,
                'amount' => $order_total*100,
                'currency' => 'usd',
                'description' => 'Grocery Service',
            ));

            $order_number = substr(str_shuffle(str_repeat("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", 10)), 0, 10);


            $order = new Order;
            $order->order_number =$order_number;
            $order->user_id =Auth::user()->id;
            $order->order_total =$order_total;
            $order->razorpay_payment_id =$charge['id'];
            $order->payment_type ='2';
            $order->order_type =$request->order_type;
            $order->ordered_date =$request->ordered_date;
            $order->status ='1';
            $order->address =$address;
            $order->building =$building;
            $order->landmark =$landmark;
            $order->pincode =$pincode;
            $order->lat =$lat;
            $order->lang =$lang;
            $order->promocode =$request->promocode;
            $order->discount_amount =$discount_amount;
            $order->discount_pr =$request->discount_pr;
            $order->tax =$request->tax;
            $order->tax_amount =$request->tax_amount;
            $order->delivery_charge =$delivery_charge;
            $order->order_notes =$request->notes;
            $order->order_from ='web';
            $order->save();

            $order_id = DB::getPdo()->lastInsertId();
            $data=Cart::where('cart.user_id',Auth::user()->id)
            ->get();

            foreach ($data as $value) {
                $OrderPro = new OrderDetails;
                $OrderPro->order_id = $order_id;
                $OrderPro->user_id = $value['user_id'];
                $OrderPro->item_id = $value['item_id'];
                $OrderPro->weight = $value['weight'];
                $OrderPro->item_name = $value['item_name'];
                $OrderPro->image_name = $value['image_name'];
                $OrderPro->price = $value['price'];
                $OrderPro->qty = $value['qty'];
                $OrderPro->item_notes = $value['item_notes'];
                $OrderPro->save();
            }
            $cart=Cart::where('user_id', Auth::user()->id)->delete();
            $count=Cart::where('user_id',Auth::user()->id)->count();

            try{
                $ordermessage='Order "'.$order_number.'" has been placed';
                $email=$getuserdata->email;
                $name=$getuserdata->name;
                $data=['ordermessage'=>$ordermessage,'email'=>$email,'name'=>$name];

                Mail::send('Email.orderemail',$data,function($message)use($data){
                    $message->from(env('MAIL_USERNAME'))->subject($data['ordermessage']);
                    $message->to($data['email']);
                } );
            }catch(\Swift_TransportException $e){
                $response = $e->getMessage() ;
                return response()->json(['status'=>0,'message'=>'Something went wrong while sending email Please try again...'],200);
            }
            
            Storage::disk('local')->put("cart", $count);

            $remove = session()->forget(['offer_amount','offer_code','order_type','address','pincode','building','landmark','notes','lat','lang','city','country','state']);

            return response()->json(['status'=>1,'message'=>'Order has been placed'],200); 

            // return 'Charge successful, you get the course!';
        } catch (\Exception $ex) {
            return $ex->getMessage();
        }
    }

    public function payWithRazorpay()
    {        
        return view('front.payWithRazorpay');
    }

    public function payment(Request $request)
    {
        
        //Input items of form
        $input = $request->all();

        $getpaymentdata=Payment::select('test_public_key','live_public_key','test_secret_key','live_secret_key','environment')->where('payment_name','RazorPay')->first();

        if ($getpaymentdata->environment=='1') {
            $razor_secret = $getpaymentdata->test_secret_key;
        } else {
            $$razor_secret = $getpaymentdata->live_secret_key;
        }

        if ($getpaymentdata->environment=='1') {
            $razor_public = $getpaymentdata->test_public_key;
        } else {
            $$razor_public = $getpaymentdata->live_public_key;
        }

        //get API Configuration 
        $api = new Api($razor_public, $razor_secret);
        //Fetch payment information by razorpay_payment_id
        $payment = $api->payment->fetch($input['razorpay_payment_id']);

        $getuserdata=User::where('id',Auth::user()->id)
        ->get()->first(); 

        if(count($input)  && !empty($input['razorpay_payment_id'])) {
            try {
                $response = $api->payment->fetch($input['razorpay_payment_id'])->capture(array('amount'=>$payment['amount']));

                $order_number = substr(str_shuffle(str_repeat("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", 10)), 0, 10);

                if ($request->order_type == "1") {
                    $delivery_charge = $request->delivery_charge;
                    $address = $request->address;
                    $lat = $request->lat;
                    $lang = $request->lang;
                    $total_amount = $request->total_amount;
                    $building = $request->building;
                    $landmark = $request->landmark;
                    $pincode = $request->pincode;
                    $city = @$city;
                    $state = @$state;
                    $country = @$country;
                    $order_total = $request->order_total;
                } else {
                    $delivery_charge = "0.00";
                    $address = 'New York, NY, USA';
                    $lat = '40.7127753';
                    $lang = '-74.0059728';
                    $order_total = $request->order_total;
                    $building = $request->building;
                    $landmark = $request->landmark;
                    $city = @$city;
                    $state = @$state;
                    $country = @$country;
                    $pincode = '10001';
                    $total_amount = $request->subtotal;
                }

                if ($request->discount_amount == "NaN") {
                    $discount_amount = "0.00";
                } else {
                    $discount_amount = $request->discount_amount;
                }

                $order = new Order;
                $order->order_number =$order_number;
                $order->user_id =Auth::user()->id;
                $order->order_total =$order_total;
                $order->razorpay_payment_id =$request->razorpay_payment_id;
                $order->payment_type ='2';
                $order->order_type =$request->order_type;
                $order->ordered_date =$request->ordered_date;                
                $order->status ='1';
                $order->address =$address;
                $order->building =$building;
                $order->landmark =$landmark;
                $order->pincode =$pincode;
                $order->lat =$lat;
                $order->lang =$lang;
                $order->promocode =$request->promocode;
                $order->discount_amount =$discount_amount;
                $order->discount_pr =$request->discount_pr;
                $order->tax =$request->tax;
                $order->tax_amount =$request->tax_amount;
                $order->delivery_charge =$delivery_charge;
                $order->order_notes =$request->notes;
                $order->order_from ='web';
                $order->save();

                $order_id = DB::getPdo()->lastInsertId();
                $data=Cart::where('cart.user_id',Auth::user()->id)
                ->get();

                foreach ($data as $value) {
                    $OrderPro = new OrderDetails;
                    $OrderPro->order_id = $order_id;
                    $OrderPro->user_id = $value['user_id'];
                    $OrderPro->item_id = $value['item_id'];
                    $OrderPro->weight = $value['weight'];
                    $OrderPro->item_name = $value['item_name'];
                    $OrderPro->image_name = $value['image_name'];
                    $OrderPro->price = $value['price'];
                    $OrderPro->qty = $value['qty'];
                    $OrderPro->item_notes = $value['item_notes'];
                    $OrderPro->save();
                }
                $cart=Cart::where('user_id', Auth::user()->id)->delete();
                $count=Cart::where('user_id',Auth::user()->id)->count();

                try{
                    $ordermessage='Order "'.$order_number.'" has been placed';
                    $email=$getuserdata->email;
                    $name=$getuserdata->name;
                    $data=['ordermessage'=>$ordermessage,'email'=>$email,'name'=>$name];

                    Mail::send('Email.orderemail',$data,function($message)use($data){
                        $message->from(env('MAIL_USERNAME'))->subject($data['ordermessage']);
                        $message->to($data['email']);
                    } );
                }catch(\Swift_TransportException $e){
                    $response = $e->getMessage() ;
                    return response()->json(['status'=>0,'message'=>'Something went wrong while sending email Please try again...'],200);
                }
                
                Storage::disk('local')->put("cart", $count);

                $remove = session()->forget(['offer_amount','offer_code','order_type','address','pincode','building','landmark','notes','lat','lang','city','country','state']);

                return response()->json(['status'=>1,'message'=>'Order has been placed'],200); 

            } catch (\Exception $e) {
                return  $e->getMessage();
                \Session::put('error',$e->getMessage());
                return redirect()->back();
            }

            // Do something here for store payment details in database...
        }
        
        \Session::put('success', 'Payment successful');
        return redirect()->back();
    }

    public function checkpincode(Request $request)
    {

        $getdata=User::select('min_order_amount','max_order_amount','currency')->where('type','1')
        ->get()->first();

        if($request->pincode != ""){
            $pincode=Pincode::select('pincode')->where('pincode',$request->pincode)
                        ->get()->first();
            if(@$pincode['pincode'] == $request->pincode) {
                if(!empty($pincode))
                {
                    if ($getdata->min_order_amount > $request->subtotal) {
                        return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
                    } elseif ($getdata->max_order_amount < $request->subtotal) {
                        return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
                    } else {
                        return response()->json(['status'=>1,'message'=>'Pincode is available for delivery'],200);
                    }                
                }
            } else {
                return response()->json(['status'=>0,'message'=>'Delivery is not available in your area'],200);
            }
        } else {
            
            if ($getdata->min_order_amount > $request->subtotal) {
                return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
            } elseif ($getdata->max_order_amount < $request->subtotal) {
                return response()->json(['status'=>0,'message'=>"Order amount must be between ".$getdata->currency."".$getdata->min_order_amount." and ".$getdata->currency."".$getdata->max_order_amount.""],200);
            } else {
                return response()->json(['status'=>1,'message'=>'Ok'],200);
            }   
        }
    }

    public function ordercancel(Request $request)
    {
        $status=Order::select('order.order_total','order.razorpay_payment_id','order.order_type','order.user_id','order.payment_type','order.user_id','order.order_total','order.order_number')
        ->join('users','order.user_id','=','users.id')
        ->where('order.id',$request['order_id'])
        ->get()->first();

        if ($status->payment_type != "0") {
            $walletdata=User::select('wallet')->where('id',$status->user_id)->first();

            $wallet = $walletdata->wallet + $status->order_total;

            $UpdateWalletDetails = User::where('id', $status->user_id)
            ->update(['wallet' => $wallet]);

            $Wallet = new Transaction;
            $Wallet->user_id = Auth::user()->id;
            $Wallet->order_id = $request->order_id;
            $Wallet->order_number = $status->order_number;
            $Wallet->wallet = $status->order_total;
            $Wallet->payment_id = $status->razorpay_payment_id;
            $Wallet->order_type = $status->order_type;
            $Wallet->transaction_type = '1';
            $Wallet->save();
        }

        $UpdateDetails = Order::where('id', $request->order_id)
                    ->update(['status' => '5']);
        
        if(!empty($UpdateDetails))
        {
            return 1;
        }
        else
        {
            return 1;
        }
    }

    public function addmoney(Request $request)
    {
        
        //Input items of form
        $input = $request->all();

        $getpaymentdata=Payment::select('test_public_key','live_public_key','test_secret_key','live_secret_key','environment')->where('payment_name','RazorPay')->first();

        if ($getpaymentdata->environment=='1') {
            $razor_secret = $getpaymentdata->test_secret_key;
        } else {
            $razor_secret = $getpaymentdata->live_secret_key;
        }

        if ($getpaymentdata->environment=='1') {
            $razor_public = $getpaymentdata->test_public_key;
        } else {
            $razor_public = $getpaymentdata->live_public_key;
        }

        //get API Configuration 
        $api = new Api($razor_public, $razor_secret);
        //Fetch payment information by razorpay_payment_id
        $payment = $api->payment->fetch($input['razorpay_payment_id']);

        $getuserdata=User::where('id',Auth::user()->id)
        ->get()->first(); 

        if(count($input)  && !empty($input['razorpay_payment_id'])) {
            try {

                $response = $api->payment->fetch($input['razorpay_payment_id'])->capture(array('amount'=>$payment['amount']));

                $wallet = $getuserdata->wallet + $input['add_money'];

                $UpdateWalletDetails = User::where('id', Auth::user()->id)
                ->update(['wallet' => $wallet]);

                if ($UpdateWalletDetails) {
                    $order_number = substr(str_shuffle(str_repeat("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", 10)), 0, 10);

                    $Wallet = new Transaction;
                    $Wallet->user_id = Auth::user()->id;
                    $Wallet->order_id = NULL;
                    $Wallet->order_number = $order_number;
                    $Wallet->wallet = $input['add_money'];
                    $Wallet->payment_id = NULL;
                    $Wallet->order_type = 3;
                    $Wallet->transaction_type = '4';
                    $Wallet->save();

                    return response()->json(['status'=>1,'message'=>'Money has been added to your Wallet'],200); 
                } else {
                    return response()->json(['status'=>0,'message'=>'Something went wrong while adding money to your wallet'],200);
                }

            } catch (\Exception $e) {
                return  $e->getMessage();
                \Session::put('error',$e->getMessage());
                return redirect()->back();
            }

            // Do something here for store payment details in database...
        }
        
        \Session::put('success', 'Payment successful');
        return redirect()->back();
    }

    public function addmoneystripe(Request $request)
    {
        
        $getuserdata=User::where('id',Auth::user()->id)
        ->get()->first();

        $location=User::select('lat','lang','map')->where('type','1')->first();

        $deal_lat=$location->lat;
        $deal_long=$location->lang;

        // Make the HTTP request
        $geocode=file_get_contents('https://maps.googleapis.com/maps/api/geocode/json?latlng='.$deal_lat.','.$deal_long.'&sensor=false&key=AIzaSyDgYRuLoA_VsDsX2kgKoc3JiUnVeu085Vo');

        $output= json_decode($geocode);
        $formattedAddress = @$output->results[0]->formatted_address;

        for($j=0;$j<count($output->results[0]->address_components);$j++){
            $cn=array($output->results[0]->address_components[$j]->types[0]);
            if(in_array("country", $cn)) {
                $country = $output->results[0]->address_components[$j]->short_name;
            }

            if(in_array("postal_code", $cn)) {
                $postal_code = $output->results[0]->address_components[$j]->long_name;
            }

            if(in_array("administrative_area_level_2", $cn)) {
                $city = $output->results[0]->address_components[$j]->long_name;
            }

            if(in_array("administrative_area_level_1", $cn)) {
                $state = $output->results[0]->address_components[$j]->short_name;
            }
        }

        $getpaymentdata=Payment::select('test_secret_key','live_secret_key','environment')->where('payment_name','Stripe')->first();

        if ($getpaymentdata->environment=='1') {
            $stripe_secret = $getpaymentdata->test_secret_key;
        } else {
            $stripe_secret = $getpaymentdata->live_secret_key;
        }

        Stripe::setApiKey($stripe_secret);

        $customer = Customer::create(array(
            'email' => Session::get('email'),
            'source' => $request->stripeToken,
            'name' => $getuserdata->name,
            'address' => [
                'line1' => $formattedAddress,
                'postal_code' => $postal_code,
                'city' => $city,
                'state' => $state,
                'country' => $country,
            ],
        ));

        $charge = Charge::create(array(
            'customer' => $customer->id,
            'amount' => $request->add_money*100,
            'currency' => 'usd',
            'description' => 'Add Money to wallet',
        ));

        $wallet = $getuserdata->wallet + $request->add_money;

        $UpdateWalletDetails = User::where('id', Auth::user()->id)
        ->update(['wallet' => $wallet]);

        if ($UpdateWalletDetails) {
            $order_number = substr(str_shuffle(str_repeat("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", 10)), 0, 10);

            $Wallet = new Transaction;
            $Wallet->user_id = Auth::user()->id;
            $Wallet->order_id = NULL;
            $Wallet->order_number = $order_number;
            $Wallet->wallet = $request->add_money;
            $Wallet->payment_id = $charge['id'];
            $Wallet->order_type = 4;
            $Wallet->transaction_type = '4';
            $Wallet->save();

            return response()->json(['status'=>1,'message'=>'Money has been added to your Wallet'],200); 
        } else {
            return response()->json(['status'=>0,'message'=>'Something went wrong while adding money to your wallet'],200);
        }
    }
}
