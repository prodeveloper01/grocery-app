<?php

namespace App\Http\Controllers\admin;

use App\Http\Controllers\Controller;
use Auth;
use Illuminate\Http\Request;
use Redirect;
use validate;
use Hash;
use Session;
use App\User;
use App\Category;
use App\Item;
use App\Ratting;
use App\Order;
use App\Promocode;

class AdminController extends Controller {
    public function login() {
        return view('login');
    }

    public function home() {
        $getcategory = Category::where('is_available','1')->where('is_deleted','2')->get();
        $getitems = Item::join('categories','item.cat_id','=','categories.id')->where('item.is_deleted','2')->where('categories.is_available','1')->where('item.item_status','1')->get();
        $getreview = Ratting::all();
        $getorders = Order::all();
        $order_total = Order::sum('order_total');
        $order_tax = Order::sum('tax_amount');
        $getpromocode = Promocode::where('is_available','1')->where('is_deleted','2')->get();
        $getusers = User::Where('type', '=' , '2')->get();
        $driver = User::Where('type', '=' , '3')->get();
        $date = date('Y-m-d');
        $future_order = Order::Where('ordered_date', '>' , $date)->count();
        $getdriver = User::where('type','3')->get();
        $todayorders = Order::with('users')->select('order.*','users.name')->leftJoin('users', 'order.driver_id', '=', 'users.id')->where('order.created_at','LIKE','%' .date("Y-m-d") . '%')->get();
        return view('home',compact('getcategory','getitems','getusers','driver','getreview','getorders','order_total','order_tax','getpromocode','todayorders','future_order','date','getdriver'));
    }

    public function getorder() {

        $todayorders = Order::with('users')
        ->where('created_at','LIKE','%' .date("Y-m-d") . '%')
        ->where('is_notification','=','1')
        ->count();
        return json_encode($todayorders);
    }

    public function clearnotification() {
        $update = Order::query()->update(["is_notification" => "2"]);

        return json_encode($update);
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
        else if($request['oldpassword']==$request['newpassword'])
        {
            $error_array[]='Old and new password must be different';
        }
        else
        {        
            if(\Hash::check($request->oldpassword,Auth::user()->password)){
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

    public function settings(request $request)
    {
        $validation = \Validator::make($request->all(), [
            'tax'=>'required',
            'currency'=>'required',
            'delivery_charge'=>'required',
            'lat'=>'required',
            'lang'=>'required',
            'max_order_qty'=>'required',
            'min_order_amount'=>'required',
            'max_order_amount'=>'required',
            'map'=>'required',
            'firebase'=>'required',
            'referral_amount'=>'required',
            'timezone'=>'required',
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
            if($request->is_open == "false") {
                $is_open = "2";
            } else {
                $is_open = "1";
            }
            $setting = User::where('id', Auth::user()->id)->update( array('tax'=>$request->tax, 'delivery_charge'=>$request->delivery_charge, 'currency'=>$request->currency, 'max_order_qty'=>$request->max_order_qty, 'min_order_amount'=>$request->min_order_amount, 'max_order_amount'=>$request->max_order_amount, 'lat'=>$request->lat, 'lang'=>$request->lang, 'map'=>$request->map, 'firebase'=>$request->firebase, 'referral_amount'=>$request->referral_amount, 'timezone'=>$request->timezone) );

            if ($setting) {
                Session::flash('message', '<div class="alert alert-success"><strong>Success!</strong> Data updated.!! </div>');
            } else {
                $error_array[]="Please try again";
            }
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        return json_encode($output);  

    }

    public function logout(Request $request) {
        Auth::logout();
        return Redirect::to('/');
    }
}
