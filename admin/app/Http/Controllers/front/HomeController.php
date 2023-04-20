<?php

namespace App\Http\Controllers\front;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\DB;
use App\Category;
use App\Item;
use App\Contact;
use App\About;
use App\Banner;
use App\User;
use App\Cart;
use App\Ratting;
use Session;
use Validator;
use Auth;

class HomeController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        @$user_id  = Auth::user()->id;

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getbanner = Banner::orderby('id','desc')->get();
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $getitem=Item::with('itemimage')->with('variation')->select('item.id','item.slug','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('item.item_status', '1')
        ->where('item.is_deleted','2')
        ->where('categories.is_available','1')
        ->orderBy('item.id', 'DESC')->paginate(10);

        $exploredata=Item::with('itemimage')->with('variation')->select('item.id','item.slug','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('categories.is_available','1')
        ->where('item.item_status', '1')
        ->where('item.is_deleted','2')
        ->inRandomOrder('1234')->paginate(10);

        $getreview = Ratting::with('users')->get();

        $getabout = About::where('id','=','1')->first();

        return view('front.home', compact('getbanner','getcategory','getitem','exploredata','getdata','cart','getreview','getabout'));
    }

    public function contact(Request $request)
    {
        if($request->firstname == ""){
            return response()->json(["status"=>0,"message"=>"First name is required"],200);
        }
        if($request->lastname == ""){
            return response()->json(["status"=>0,"message"=>"Last name is required"],200);
        }
        if($request->email == ""){
            return response()->json(["status"=>0,"message"=>"Email is required"],200);
        }
        if($request->message == ""){
            return response()->json(["status"=>0,"message"=>"Message is required"],200);
        }
        $contcat = new Contact;
        $contcat->firstname =$request->firstname;
        $contcat->lastname =$request->lastname;
        $contcat->email =$request->email;
        $contcat->message =$request->message;
        $contcat->save();

        if ($contcat) {
            return response()->json(['status'=>1,'message'=>'Your message has been successfully sent.!'],200);
        } else {
            return response()->json(['status'=>2,'message'=>'Something went wrong.'],200);
        }
    }
}
