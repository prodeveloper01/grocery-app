<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Redirect;
use App\PrivacyPolicy;
use App\Category;
use App\TermsCondition;
use App\About;
use App\User;
use App\Item;
use App\Ratting;
use App\Order;
use App\Promocode;

class HomeController extends Controller
{
    /**
     * Create a new controller instance.
     *
     * @return void
     */
    // public function __construct()
    // {
    //     $this->middleware('auth');
    // }

    public function policy() {
        $getprivacypolicy = PrivacyPolicy::where('id', '1')->first();
        return view('privacy-policy', compact('getprivacypolicy'));
    }

    public function terms() {
        $gettermscondition = TermsCondition::where('id', '1')->first();
        return view('terms-condition', compact('gettermscondition'));
    }

    public function aboutus() {
        $getabout = About::where('id', '1')->first();
        return view('about-us', compact('getabout'));
    }

    /**
     * Show the application dashboard.
     *
     * @return \Illuminate\Contracts\Support\Renderable
     */
    public function index()
    {
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
        $getdriver = User::where('type','3')->get();
        $future_order = Order::Where('ordered_date', '>' , $date)->count();
        $todayorders = Order::with('users')->select('order.*','users.name')->leftJoin('users', 'order.driver_id', '=', 'users.id')->where('order.created_at','LIKE','%' .date("Y-m-d") . '%')->get();
        return view('home',compact('getcategory','getitems','getusers','driver','getreview','getorders','order_total','order_tax','getpromocode','todayorders','future_order','date','getdriver'));
    }

    public function auth(Request $request)
    {
		return Redirect::to('/')->with('success', 'You have successfully verified your License. Please try to Login now. Nulled by nullcave.club');
    }
}
