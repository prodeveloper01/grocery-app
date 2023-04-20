<?php

namespace App\Http\Controllers\front;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Category;
use App\Cart;
use App\Favorite;
use App\About;
use App\User;
use Auth;

class FavoriteController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index() {

        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $favorite=Favorite::with('itemimage')->select('favorite.id as favorite_id','item.id','item.item_name','item.slug','item.item_description')
        ->join('item','favorite.item_id','=','item.id')
        ->where('item.item_status','1')
        ->where('favorite.user_id',$user_id)->paginate(9);

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getabout = About::where('id','=','1')->first();

        if (Auth::guest()) {
            return view('front.login', compact('cart','getdata','getcategory','getabout'));
        } else {
            return view('front.favorite',compact('favorite','cart','getdata','getcategory','getabout'));
        }
    }
}
