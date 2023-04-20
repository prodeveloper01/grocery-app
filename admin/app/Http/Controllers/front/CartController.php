<?php

namespace App\Http\Controllers\front;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\DB;
use App\Item;
use App\Cart;
use App\Category;
use App\Promocode;
use App\Payment;
use App\User;
use App\Order;
use App\About;
use App\Address;
use Storage;
use Validator;
use Illuminate\Support\Facades\Redirect;
use Auth;

class CartController extends Controller
{
    public function index() {
        @$user_id  = Auth::user()->id;
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','delivery_charge','currency','tax','map')->where('type','1')
            ->get()->first();

        $addressdata=Address::where('user_id',$user_id)->orderBy('id', 'DESC')->get();

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $getpromocode=Promocode::select('promocode.offer_name','promocode.offer_code','promocode.offer_amount','promocode.description')
        ->where('is_available','=','1')
        ->get();

        $getabout = About::where('id','=','1')->first();

        return view('front.cart', compact('getcategory','cart','getdata','getpromocode','getabout','addressdata'));
    }

    public function list()
    {
        @$user_id  = Auth::user()->id;
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','delivery_charge','currency','tax','map')->where('type','1')
            ->get()->first();
        $getabout = About::where('id','=','1')->first();
        return view('front.theme.right-cart',compact('cart','getdata','getabout'));
    }

    public function addtocart(Request $request)
    {   
        if($request->item_id == ""){
            return response()->json(["status"=>0,"message"=>"Please select Item"],200);
        }
        if($request->variation_id == ""){
            return response()->json(["status"=>0,"message"=>"Please select variation"],200);
        }

        $text= $request->variation;
        $strings = str_replace('–', '-', $text);
        $strings = explode('-',$text);

        $data=Cart::where('user_id',Auth::user()->id)
                ->where('item_id', $request->item_id)
                ->where('variation_id', $request->variation_id)
                ->first();

        if (!empty($data)) {

            $result = DB::table('cart')
            ->where('user_id',Auth::user()->id)
            ->where('item_id', $request->item_id)
            ->where('variation_id', $request->variation_id)
            ->update([
                'qty' => $data->qty+1,
            ]);
            return response()->json(['status'=>1,'message'=>'Qty has been update'],200);
        } else {
            $getitem=Item::with('itemimage')->select('item.id','item.item_name')
            ->where('item.id',$request->item_id)->first();

            $cart = new Cart;
            $cart->item_id =$request->item_id;
            $cart->item_name =$getitem->item_name;
            $cart->image_name =$getitem['itemimage']->image_name;
            $cart->qty ='1';
            $cart->price =$strings[1];
            $cart->weight =$strings[0];
            $cart->user_id =Auth::user()->id;
            $cart->variation_id =$request->variation_id;
            $cart->save();
            $count=Cart::where('user_id',Auth::user()->id)->count();
            Storage::disk('local')->put("cart", $count);
            return response()->json(['status'=>1,'message'=>'Item is added to your cart','cartcnt'=>$count],200);
        }
    }

    public function delete(Request $request)
    {
        if($request->id == ""){
            return response()->json(["status"=>0,"message"=>"Cart Id is required"],200);
        }

        $cart=Cart::where('id', $request->id)->delete();

        $count=Cart::where('user_id',Auth::user()->id)->count();
        Storage::disk('local')->put("cart", $count);

        if($cart)
        {
            return response()->json(['status'=>1,'message'=>'Item is added to your cart','cartcnt'=>$count],200);
        }
        else
        {
            return response()->json(['status'=>0],200);
        }
    }

    public function qtyupdate(Request $request)
    {
        if($request->cart_id == ""){
            return response()->json(["status"=>0,"message"=>"Cart ID is required"],200);
        }
        if($request->qty == ""){
            return response()->json(["status"=>0,"message"=>"Qty is required"],200);
        }

        $cartdata=Cart::where('id', $request->cart_id)
        ->get()
        ->first();

        if ($request->type == "decreaseValue") {
            $qty = $cartdata->qty-1;
        } else {
            $qty = $cartdata->qty+1;
        }

        $update=Cart::where('id',$request['cart_id'])->update(['qty'=>$qty]);

        return response()->json(['status'=>1,'message'=>'Qty has been update'],200);
    }

    public function applypromocode(Request $request)
    {
        if($request->promocode == ""){
            return response()->json(["status"=>0,"message"=>"Promocode is required"],200);
        }

        @$user_id  = Auth::user()->id;

        $checkpromo=Order::select('promocode')->where('promocode',$request->promocode)->where('user_id',$user_id)
        ->count();

        if ($checkpromo > "0" ) {
            return response()->json(['status'=>0,'message'=>'The Offer Is Applicable Only Once Per User'],200);
        } else {
            $promocode=Promocode::select('promocode.offer_amount','promocode.description','promocode.offer_code')->where('promocode.offer_code',$request['promocode'])
            ->get()->first();

                session ( [ 
                    'offer_amount' => $promocode->offer_amount, 
                    'offer_code' => $promocode->offer_code,
                ] );

            if($promocode['offer_code']== $request->promocode) {
                if(!empty($promocode))
                {
                    return response()->json(['status'=>1,'message'=>'Promocode has been applied','data'=>$promocode],200);
                }
            } else {
                return response()->json(['status'=>0,'message'=>'You applied wrong Promocode'],200);
            }
        }
    }

    public function removepromocode(Request $request)
    {
        
        $remove = session()->forget(['offer_amount','offer_code']);

        if(!$remove) {
            return response()->json(['status'=>1,'message'=>'Promo Code has been removed'],200);
        } else {
            return response()->json(['status'=>0,'message'=>'Something went wrong.'],200);
        }
    }

    public function checkoutdata(Request $request) {
        if ($request->order_type == "1") {
            if ($request->order_type == "") {
                return response()->json(['status'=>0,'message'=>'Please select Pickup OR Delivery'],200);
            }
            if ($request->address == "") {
                return response()->json(['status'=>0,'message'=>'Please select Address'],200);
            }
            if ($request->pincode == "") {
                return response()->json(['status'=>0,'message'=>'Please select Pincode'],200);
            }
            if ($request->building == "") {
                return response()->json(['status'=>0,'message'=>'Please select building'],200);
            }
            if ($request->landmark == "") {
                return response()->json(['status'=>0,'message'=>'Please select Landmark'],200);
            }

            if ($request->lat == "" && $request->lang == "") {
                return response()->json(['status'=>0,'message'=>'Please select address from suggestion'],200);
            }
        }

        session ( [ 
            'order_type' => $request->order_type,
            'address' => $request->address,
            'pincode' => $request->pincode,
            'building' => $request->building,
            'landmark' => $request->landmark,
            'notes' => $request->notes,
            'lat' => $request->lat,
            'lang' => $request->lang,
            'city' => $request->city,
            'country' => $request->country,
            'state' => $request->state,
            'ordered_date' => $request->ordered_date,
        ] );

        return response()->json(['status'=>1,'message'=>'Success'],200);
    }


    public function checkout(Request $request) {
        @$user_id  = Auth::user()->id;
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $userinfo=User::select('name','email','mobile','wallet')->where('id',$user_id)
        ->get()->first();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','delivery_charge','currency','tax')->where('type','1')
            ->get()->first();

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $getpaymentdata=Payment::select('payment_name','test_public_key','live_public_key','environment')->where('is_available','1')->orderBy('id', 'DESC')->get();

        $getabout = About::where('id','=','1')->first();

        return view('front.checkout', compact('getcategory','cart','getdata','getpaymentdata','userinfo','getabout'));
    }
}
