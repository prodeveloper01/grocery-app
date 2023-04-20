<?php

namespace App\Http\Controllers\front;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Redirect;
use Illuminate\Support\Facades\DB;
use Session;
use Auth;
use App\About;
use App\User;
use App\Category;
use App\Ratting;
use App\Transaction;
use App\Pincode;
use App\Payment;
use App\Address;
use App\Cart;
use Validator;
use Storage;

class UserController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index() {
        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();
        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();
        $getabout = About::where('id','=','1')->first();
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();
        return view('front.login', compact('getcategory','getdata','getabout','cart'));
    }

    public function signup() {
        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();
        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();
        $getabout = About::where('id','=','1')->first();
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();
        return view('front.signup', compact('getcategory','getdata','getabout','cart'));
    }

    public function account()
    {
        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();
        $getabout = About::where('id','=','1')->first();

        if (Auth::guest()) {
            return view('front.login', compact('cart','getdata','getcategory','getabout'));
        } else {
            return view('front.account', compact('cart','getdata','getcategory','getabout'));
        }        
    }

    public function updateaccount(request $request)
    {
        if ($request->newpassword =="") {
            
            $validation = Validator::make($request->all(),[
                'name'=>'required'
            ],[
                'name.required'=>'Name is required'
            ]);
             
            if ($validation->fails())
            {
                 return Redirect::back()->withErrors($validation, 'account')->withInput();
            }
            else if($request->oldpassword==$request->newpassword)
            {
                return Redirect::back()->with('danger', 'Old and new password must be different');
            }
            else
            {
                $update=User::where('email',$request['email'])->update(['name'=>$request->name]);

                session ( [
                    'name' => $request->name
                ] );

                return redirect()->back()->with('message', 'Profile has been updated');
            }
        } else {
            $validation = Validator::make($request->all(),[
                'name'=>'required',
                'oldpassword'=>'required|min:6',
                'newpassword'=>'required|min:6',
                'confirmpassword'=>'required_with:newpassword|same:newpassword|min:6',
            ],[
                'name.required'=>'Name is required',
                'oldpassword.required'=>'Old Password is required',
                'newpassword.required'=>'New Password is required',
                'confirmpassword.required'=>'Confirm Password is required'
            ]);
             
            if ($validation->fails())
            {
                return Redirect::back()->withErrors($validation, 'account')->withInput();
            }
            else if($request->oldpassword==$request->newpassword)
            {
                return Redirect::back()->with('danger', 'Old and new password must be different');
            }
            else
            {
                $login=User::where('id','=',Auth::user()->id)->first();
                if(\Hash::check($request->oldpassword,$login->password)){

                    $update=User::where('email',$request['email'])->update(['password'=>Hash::make($request->newpassword),'name'=>$request->name]);

                    return redirect()->back()->with('message', 'Profile has been updated');
                }else{
                    return Redirect::back()->with('danger', 'Old Password is not match.');
                }
            }
        }
        
        return Redirect::back()->with('danger', 'Something went wrong');
    }

    public function login(Request $request)
    {
        $validation = Validator::make($request->all(),[
            'email' => 'required',
            'password' => 'required',
        ]);
        if ($validation->fails())
        {
            return Redirect::back()->withErrors($validation, 'login')->withInput();
        }

        $login=User::where('email',$request['email'])->where('type','=','2')->first();
        
        if(!empty($login)) {
            if(Hash::check($request->get('password'),$login->password)) {
            
                if($login->is_verified == '1') {
                    if($login->is_available == '1') {
                        $auth=Auth::attempt(['email' => $request->email, 'password' => $request->password]);
                        $cart=Cart::where('user_id',Auth::user()->id)->count();

                        Storage::disk('local')->put("cart", $cart);

                        return Redirect::to('/');
                    } else {
                        return Redirect::back()->with('danger', 'Your account has been blocked by Admin');
                    }
                } else {
                    $otp = rand ( 100000 , 999999 );
                    try{

                        $updatedata['otp'] = $otp;
                        User::where('id', $login->id)->update($updatedata);

                        $title='Email Verification Code';
                        $email=$request->email;
                        $data=['title'=>$title,'email'=>$email,'otp'=>$otp];

                        Mail::send('Email.emailverification',$data,function($message)use($data){
                            $message->from(env('MAIL_USERNAME'))->subject($data['title']);
                            $message->to($data['email']);
                        } );

                        if (env('Environment') == 'sendbox') {
                            session ( [
                                'email' => $login->email,
                                'password' => $login->password,
                                'otp' => $otp,
                            ] );
                        } else {
                            session ( [
                                'email' => $login->email,
                                'password' => $login->password,
                            ] );
                        }

                        
                        return Redirect::to('/email-verify')->with('success', "OTP has been sent to your registered Email ID");

                    }catch(Exception $e){
                        return Redirect::back()->with('danger', 'Something went wrong');
                    }
                }
            } else {
                return Redirect::back()->with('danger', 'Password is incorrect');
            }
        } else {
            return Redirect::back()->with('danger', 'Email is incorrect');
        }     
    }

    public function register(Request $request)
    {
        if (Session::get('facebook_id') OR Session::get('google_id')) {
            $validation = Validator::make($request->all(),[
                'name' => 'required',
                'email' => 'required',
                'mobile' => 'required',
                'accept' =>'accepted'
            ]);
            if ($validation->fails())
            {
                return Redirect::back()->withErrors($validation, 'login')->withInput();
            }
            else
            {
                $str_result = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz'; 
                $referral_code = substr(str_shuffle($str_result), 0, 10); 
                $otp = rand ( 100000 , 999999 );

                $checkreferral=User::select('id','name','referral_code','wallet')->where('referral_code',$request['referral_code'])->first();

                if (@$checkreferral->referral_code == $request['referral_code']) {

                    $users=User::where('email',$request->email)->get()->first();
                    try{
                        try{
                            $otp = rand ( 100000 , 999999 );

                            $title='Email Verification Code';
                            $email=$request->email;
                            $data=['title'=>$title,'email'=>$email,'otp'=>$otp];

                            Mail::send('Email.emailverification',$data,function($message)use($data){
                                $message->from(env('MAIL_USERNAME'))->subject($data['title']);
                                $message->to($data['email']);
                            } );
                            
                            User::where('email', $request->email)->update(['otp'=>$otp,'mobile'=>$request->mobile,'referral_code'=>$referral_code]);

                            if ($request['referral_code'] != "") {
                                $getdata=User::select('referral_amount')->where('type','1')->get()->first();

                                $wallet = $checkreferral->wallet + $getdata->referral_amount;

                                if ($wallet) {
                                    $UpdateWalletDetails = User::where('id', $checkreferral->id)
                                    ->update(['wallet' => $wallet]);

                                    $from_Wallet = new Transaction;
                                    $from_Wallet->user_id = $checkreferral->id;
                                    $from_Wallet->order_id = null;
                                    $from_Wallet->order_number = null;
                                    $from_Wallet->wallet = $getdata->referral_amount;
                                    $from_Wallet->payment_id = null;
                                    $from_Wallet->order_type = '0';
                                    $from_Wallet->transaction_type = '3';
                                    $from_Wallet->username = $request->name;
                                    $from_Wallet->save();
                                }

                                if ($getdata->referral_amount) {
                                    $UpdateWallet = User::where('id', $users->id)
                                    ->update(['wallet' => $getdata->referral_amount]);

                                    $to_Wallet = new Transaction;
                                    $to_Wallet->user_id = $users->id;
                                    $to_Wallet->order_id = null;
                                    $to_Wallet->order_number = null;
                                    $to_Wallet->wallet = $getdata->referral_amount;
                                    $to_Wallet->payment_id = null;
                                    $to_Wallet->order_type = '0';
                                    $to_Wallet->transaction_type = '3';
                                    $to_Wallet->username = $checkreferral->name;
                                    $to_Wallet->save();
                                }
                            }

                            if (env('Environment') == 'sendbox') {
                                session ( [
                                    'email' => $request->email,
                                    'otp' => $otp,
                                ] );
                            } else {
                                session ( [
                                    'email' => $request->email,
                                ] );
                            }
                            
                            return Redirect::to('/email-verify')->with('success', 'Email has been sent to your registered email address');  
                        }catch(\Swift_TransportException $e){
                            $response = $e->getMessage() ;
                            return Redirect::back()->with('danger', 'Something went wrong while sending email Please try again...');
                        }  
                    }catch(\Swift_TransportException $e){
                        $response = $e->getMessage() ;
                        return Redirect::back()->with('danger', 'Something went wrong while sending email Please try again...');
                    }
                } else {
                    return redirect()->back()->with('danger', 'Invalid Referral Code');
                }
            }
            return Redirect::back()->withErrors(['msg', 'Something went wrong']);
        } else {
            $validation = Validator::make($request->all(),[
                'name' => 'required',
                'email' => 'required|unique:users',
                'mobile' => 'required|unique:users',
                'password' => 'required|confirmed',
                'accept' =>'accepted'
            ]);
            if ($validation->fails())
            {
                return Redirect::back()->withErrors($validation, 'login')->withInput();
            }
            else
            {
                $str_result = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz'; 
                $referral_code = substr(str_shuffle($str_result), 0, 10); 
                $otp = rand ( 100000 , 999999 );

                $checkreferral=User::select('id','name','referral_code','wallet')->where('referral_code',$request['referral_code'])->first();

                if (@$checkreferral->referral_code == $request['referral_code']) {

                    try{
                        $title='Email Verification Code';
                        $email=$request->email;
                        $data=['title'=>$title,'email'=>$email,'otp'=>$otp];

                        Mail::send('Email.emailverification',$data,function($message)use($data){
                            $message->from(env('MAIL_USERNAME'))->subject($data['title']);
                            $message->to($data['email']);
                        } );

                        $user = new User;
                        $user->name =$request->name;
                        $user->email =$request->email;
                        $user->mobile =$request->mobile;
                        $user->profile_image ='unknown.png';
                        $user->login_type ='email';
                        $user->otp=$otp;
                        $user->type ='2';
                        $user->referral_code=$referral_code;
                        $user->password =Hash::make($request->password);
                        $user->save();

                        if ($request['referral_code'] != "") {
                            $getdata=User::select('referral_amount')->where('type','1')->get()->first();

                            $wallet = $checkreferral->wallet + $getdata->referral_amount;

                            if ($wallet) {
                                $UpdateWalletDetails = User::where('id', $checkreferral->id)
                                ->update(['wallet' => $wallet]);

                                $from_Wallet = new Transaction;
                                $from_Wallet->user_id = $checkreferral->id;
                                $from_Wallet->order_id = null;
                                $from_Wallet->order_number = null;
                                $from_Wallet->wallet = $getdata->referral_amount;
                                $from_Wallet->payment_id = null;
                                $from_Wallet->order_type = '0';
                                $from_Wallet->transaction_type = '3';
                                $from_Wallet->username = $user->name;
                                $from_Wallet->save();
                            }

                            if ($getdata->referral_amount) {
                                $UpdateWallet = User::where('id', $user->id)
                                ->update(['wallet' => $getdata->referral_amount]);

                                $to_Wallet = new Transaction;
                                $to_Wallet->user_id = $user->id;
                                $to_Wallet->order_id = null;
                                $to_Wallet->order_number = null;
                                $to_Wallet->wallet = $getdata->referral_amount;
                                $to_Wallet->payment_id = null;
                                $to_Wallet->order_type = '0';
                                $to_Wallet->transaction_type = '3';
                                $to_Wallet->username = $checkreferral->name;
                                $to_Wallet->save();
                            }
                        }

                        if (env('Environment') == 'sendbox') {
                            session ( [
                                'email' => $request->email,
                                'otp' => $otp,
                            ] );
                        } else {
                            session ( [
                                'email' => $request->email,
                            ] );
                        }
                        return Redirect::to('/email-verify')->with('success', 'Email has been sent to your registered email address');  
                    }catch(\Swift_TransportException $e){
                        $response = $e->getMessage() ;
                        return Redirect::back()->with('danger', 'Something went wrong while sending email Please try again...');
                    }
                } else {
                    return redirect()->back()->with('danger', 'Invalid Referral Code');
                }
            }
            return redirect()->back()->with('danger', 'Something went wrong');
        }
    }

    public function changePassword(request $request)
    {
        $validation = \Validator::make($request->all(), [
            'oldpassword'=>'required|min:6',
            'newpassword'=>'required|min:6',
            'confirmpassword'=>'required_with:newpassword|same:newpassword|min:6',
        ],[
            'oldpassword.required'=>'Old Password is required',
            'newpassword.required'=>'New Password is required',
            'confirmpassword.required'=>'Confirm Password is required'
        ]);
         
        $error_array = array();
        $success_output = '';
        if ($validation->fails())
        {
            foreach($validation->messages()->getMessages() as $field_name => $messages)
            {
                $error_array[] = $messages;
            }
        }
        else if($request->oldpassword==$request->newpassword)
        {
            $error_array[]='Old and new password must be different';
        }
        else
        {
            $login=User::where('id','=',Auth::user()->id)->first();

            if(\Hash::check($request->oldpassword,$login->password)){
                $data['password'] = Hash::make($request->newpassword);
                User::where('id', Auth::user()->id)->update($data);
                Session::flash('message', '<div class="alert alert-success"><strong>Success!</strong> Password has been changed.!! </div>');
            }else{
                $error_array[]="Old Password is not match.";
            }
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        return json_encode($output);  

    }

    public function addreview(request $request)
    {
        $validation = \Validator::make($request->all(), [
            'user_id' => 'required|unique:ratting',
            'ratting'=>'required',
            'comment'=>'required',
        ],[
            'user_id.unique'=>'You already given review',
            'ratting.required'=>'Rating is required',
            'comment.required'=>'Comment is required'
        ]);
         
        $error_array = array();
        $success_output = '';
        if ($validation->fails())
        {
            foreach($validation->messages()->getMessages() as $field_name => $messages)
            {
                $error_array[] = $messages;
            }
        }
        else
        {
            $user = new Ratting;
            $user->user_id =$request->user_id;
            $user->ratting =$request->ratting;
            $user->comment =$request->comment;
            $user->save();
            Session::flash('message', '<div class="alert alert-success"><strong>Success!</strong> Review has been added.!! </div>');
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        
        return json_encode($output);  

    }

    public function forgot_password() {
        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();
        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getabout = About::where('id','=','1')->first();
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();
        return view('front.forgot-password', compact('getcategory','getdata','getabout','cart'));
    }

    public function forgotpassword(Request $request)
    {
        $validation = Validator::make($request->all(),[
            'email' => 'required'
        ]);
        if ($validation->fails())
        {
            return Redirect::back()->withErrors($validation, 'login')->withInput();
        }
        else
        {
            $checklogin=User::where('email',$request->email)->first();
            
            if(empty($checklogin))
            {
                return Redirect::back()->with('danger', 'Email does not exist');
            } else {
                if ($checklogin->google_id != "" OR $checklogin->facebook_id != "") {
                    return Redirect::back()->with('danger', 'Your account has been registered with social media');
                } else {
                    try{
                        $password = substr(str_shuffle('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789') , 0 , 8 );

                        $newpassword['password'] = Hash::make($password);
                        $update = User::where('email', $request['email'])->update($newpassword);
                        
                        $title='Password Reset';
                        $email=$checklogin->email;
                        $name=$checklogin->name;
                        $data=['title'=>$title,'name'=>$name,'email'=>$email,'password'=>$password];

                        Mail::send('Email.email',$data,function($message)use($data){
                            $message->from(env('MAIL_USERNAME'))->subject($data['title']);
                            $message->to($data['email']);
                        } );
                        return Redirect::to('/signin')->with('success', 'New password has been sent to your email..');
                    }catch(\Swift_TransportException $e){
                        $response = $e->getMessage() ;
                        return Redirect::back()->with('danger', 'Something went wrong while sending email Please try again...');
                    }
                }
            }
        }
        return Redirect::back()->with('danger', 'Something went wrong'); 
    }

    public function email_verify() {
        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();
        $getabout = About::where('id','=','1')->first();
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();
        return view('front.email-verify', compact('getcategory','getdata','getabout','cart'));
    }

    public function email_verification(Request $request)
    {
        $validation = Validator::make($request->all(),[
            'email' => 'required',
            'otp' => 'required',
        ]);
        if ($validation->fails())
        {
            return Redirect::back()->withErrors($validation, 'emailverify')->withInput();
        }
        else
        {
            $checkuser=User::where('email',$request->email)->first();

            if (!empty($checkuser)) {
                if ($checkuser->otp == $request->otp) {
                    $update=User::where('email',$request['email'])->update(['otp'=>NULL,'is_verified'=>'1']);

                    $cart=Cart::where('user_id',$checkuser->id)->count();

                    Storage::disk('local')->put("cart", $cart);

                    $auth=Auth::login($checkuser);

                    return Redirect::to('/');

                } else {
                    return Redirect::back()->with('danger', 'Invalid OTP');
                }
            } else {
                return Redirect::back()->with('danger', 'Invalid Email ID');
            }            
        }
        return Redirect::back()->with('danger', 'Something went wrong'); 
    }

    public function resend_email()
    {
        $checkuser=User::where('email',Session::get('email'))->first();

        if (!empty($checkuser)) {
            try{
                $otp = rand ( 100000 , 999999 );
                $update=User::where('email',Session::get('email'))->update(['otp'=>$otp,'is_verified'=>'2']);

                $title='Email Verification Code';
                $email=Session::get('email');
                $data=['title'=>$title,'email'=>$email,'otp'=>$otp];
                Mail::send('Email.emailverification',$data,function($message)use($data){
                    $message->from(env('MAIL_USERNAME'))->subject($data['title']);
                    $message->to($data['email']);
                } );

                if (env('Environment') == 'sendbox') {
                    session ( [
                        'otp' => $otp,
                    ] );
                }

                return Redirect::to('/email-verify')->with('success', "OTP has been sent to your registered Email ID");
            }catch(Exception $e){
                $response = $e->getMessage() ;
                return Redirect::back()->with('danger', 'Something went wrong while sending email Please try again...');
            }
        } else {
            return Redirect::back()->with('danger', 'Invalid Email ID');
        }  
    }

    public function wallet(Request $request)
    {

        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $walletamount=User::select('wallet')->where('id',$user_id)->first();

        $getpaymentdata=Payment::select('payment_name','test_public_key','live_public_key','environment')->where('is_available','1')->orderBy('id', 'DESC')->get();

        $transaction_data=Transaction::select('order_number','transaction_type','order_type','wallet',DB::raw('DATE_FORMAT(created_at, "%d %M %Y") as date'),'username','order_id')->where('user_id',$user_id)->orderBy('id', 'DESC')->paginate(6);
        $getabout = About::where('id','=','1')->first();
        if (Auth::guest()) {
            return view('front.login', compact('cart','getdata','getcategory','getabout'));
        } else {
            return view('front.wallet', compact('getcategory','cart','transaction_data','walletamount','getdata','getabout','getpaymentdata'));
        }
    }

    public function address(Request $request)
    {
        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $addressdata=Address::where('user_id',Auth::user()->id)->orderBy('id', 'DESC')->paginate(6);

        $getabout = About::where('id','=','1')->first();

        $getdata=User::select('currency')->where('type','1')->first();

        $getpincode = Pincode::get();
        return view('front.address', compact('getabout','addressdata','getdata','getpincode','getcategory','cart'));
    }

    public function show(Request $request)
    {
        $getaddress = Address::where('id',$request->id)->first();
        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Address fetch successfully', 'ResponseData' => $getaddress], 200);
    }

    public function addaddress(Request $request)
    {
        $pincode=Pincode::select('pincode')->where('pincode',$request->pincode)
        ->get()->first();

        if(@$pincode['pincode'] == $request->pincode) {
            try {

                $address = new Address;
                $address->user_id = Auth::user()->id;
                $address->full_name = $request->full_name;
                $address->address_type = $request->address_type;
                $address->address = $request->address;
                $address->lat = $request->lat;
                $address->lang = $request->lang;
                $address->landmark = $request->landmark;
                $address->building = $request->building;
                $address->mobile = $request->mobile;
                $address->pincode = $request->pincode;
                $address->save();

                return Redirect::back()->with('success', 'Address has been save.');
                
            } catch (Exception $e) {
                $response = $e->getMessage() ;
                return Redirect::back()->with('danger', 'Something went wrong while saving address Please try again...');
            }
        } else {
            return Redirect::back()->with('danger', 'Delivery is not available in your area');
        }
    }

    public function editaddress(Request $request)
    {
        $validation = Validator::make($request->all(),[
            'full_name' => 'required',
            'address_type' => 'required',
            'address' => 'required',
            'landmark' =>'required',
            'building' =>'required',
            'mobile' =>'required',
            'pincode' =>'required'
        ]);
        if ($validation->fails())
        {
            return Redirect::back()->withErrors($validation, 'login')->withInput();
        }
        else
        {
            $pincode=Pincode::select('pincode')->where('pincode',$request->pincode)
            ->get()->first();

            if(@$pincode['pincode'] == $request->pincode) {

                try {

                    Address::where('id', $request->id)->update(['full_name'=>$request->full_name,'address_type'=>$request->address_type,'address'=>$request->address,'lat'=>$request->lat,'lang'=>$request->lang,'landmark'=>$request->landmark,'building'=>$request->building,'mobile'=>$request->mobile,'pincode'=>$request->pincode]);


                    return Redirect::back()->with('success', 'Address has been update.');
                    
                } catch (Exception $e) {
                    $response = $e->getMessage() ;
                    return Redirect::back()->with('danger', 'Something went wrong while saving address Please try again...');
                }
            } else {
                return Redirect::back()->with('danger', 'Delivery is not available in your area');
            }

        }
        return Redirect::back()->with('danger', 'Something went wrong');
    }

    public function delete(Request $request)
    {
        $address=Address::where('id', $request->id)->where('user_id', $request->user_id)->delete();
        if ($address) {
            return 1;
        } else {
            return 0;
        }
    }

    public function logout() {
        Session::flush ();
        Auth::logout ();
        return Redirect::to('/');
    }
}
