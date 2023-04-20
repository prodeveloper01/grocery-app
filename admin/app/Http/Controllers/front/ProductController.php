<?php

namespace App\Http\Controllers\front;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\DB;
use App\Item;
use App\User;
use App\About;
use App\ItemImages;
use App\Category;
use App\Favorite;
use App\Cart;
use Session;
use Validator;
use Auth;

class ProductController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function category()
    {
        @$user_id  = Auth::user()->id;

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->paginate(15);

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();
        $getabout = About::where('id','=','1')->first();

        return view('front.category', compact('getdata','getcategory','cart','getabout'));
    }

    public function latest()
    {
        @$user_id  = Auth::user()->id;

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getitem=Item::with('itemimage')->with('variation')->select('item.id','item.item_name','item.slug',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('item.item_status', '1')
        ->where('item.is_deleted','2')
        ->where('categories.is_available','1')
        ->orderBy('item.id', 'DESC')->paginate(15);

        $getabout = About::where('id','=','1')->first();

        return view('front.latest-products', compact('getitem','getdata','getcategory','cart','getabout'));
    }

    public function search(Request $request)
    {
        @$user_id  = Auth::user()->id;

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getitem=Item::with('itemimage')->with('variation')->select('item.id','item.item_name','item.slug',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('item.item_status', '1')
        ->where('item.is_deleted','2')
        ->where('categories.is_available','1')
        ->where('item.item_name','LIKE','%' . $request->item . '%')
        ->orderBy('item.id', 'DESC')->paginate(15);

        $getabout = About::where('id','=','1')->first();


        $getdata=User::select('currency')->where('type','1')->first();
        return view('front.search', compact('getitem','getdata','getcategory','cart','getabout'));
    }

    public function explore()
    {
        @$user_id  = Auth::user()->id;
        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $exploredata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name','item.slug',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('categories.is_available','1')
        ->where('item.item_status', '1')
        ->where('item.is_deleted','2')
        ->inRandomOrder('1234')->paginate(15);

        $getabout = About::where('id','=','1')->first();

        return view('front.explore-products', compact('exploredata','getdata','getcategory','cart','getabout'));
    }

    public function show(Request $request)
    {
        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getitem=Item::with('itemimage')->with('variation')->select('item.id','item.item_name','item.slug','item.item_description','categories.category_name')
        ->join('categories','item.cat_id','=','categories.id')
        ->where('item.id',$request->id)->first();

        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Success', 'ResponseData' => $getitem,'currency' => $getdata->currency], 200);
    }

    public function productdetails(Request $request, $slug) {
        $user_id  = @Auth::user()->id;

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getitem=Item::with('itemimage')->with('variation')->select('item.id','item.item_name','item.slug','item.item_description','item.brand','item.manufacturer','item.country_origin','item.ingredient_type','item.delivery_time','categories.category_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('item.slug',$slug)->first();

        if(empty($getitem)){ 
            abort(404); 
        } else {

            $relatedproduct = Item::with(['category','itemimage','variation'])->select('item.id','item.item_name','item.slug',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
            ->leftJoin('favorite', function($query) use($user_id) {
                $query->on('favorite.item_id','=','item.id')
                ->where('favorite.user_id', '=', $user_id);
            })
            ->where('item.item_status', '1')
            ->where('item.is_deleted','2')
            ->where('item.id','!=',$getitem->id)->orderBy('id', 'DESC')->paginate(10);

            $getimages = ItemImages::select(\DB::raw("CONCAT('".url('/public/images/item/')."/', image) AS image"))->where('item_id','=',$getitem->id)->get();

            $getabout = About::where('id','=','1')->first();

            return view('front.product-details', compact('getitem','getdata','getimages','relatedproduct','getcategory','cart','getabout'));
        }
    }

    public function products(Request $request, $slug) {
        $user_id  = @Auth::user()->id;

        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','item.slug','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getitem=Item::with('itemimage')->select('item.id','item.item_name','item.slug','item.item_description','item.brand','item.manufacturer','item.country_origin','item.ingredient_type','item.delivery_time','categories.category_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('categories.slug',$slug)->paginate(15);
        $getabout = About::where('id','=','1')->first();

        $getcatname=Category::select('category_name')
        ->where('slug',$slug)->first();

        if(empty($getitem)){ 
            abort(404); 
        } else {
            return view('front.products', compact('getitem','getdata','getcategory','cart','getabout','getcatname'));
        }
    }

    public function favorite(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }
        if($request->item_id == ""){
            return response()->json(["status"=>0,"message"=>"Item is required"],400);
        }

        $data=Favorite::where([
            ['favorite.user_id',$request['user_id']],
            ['favorite.item_id',$request['item_id']]
        ])
        ->get()
        ->first();
        try {
            if ($data=="") {
                $favorite = new Favorite;
                $favorite->user_id =$request->user_id;
                $favorite->item_id =$request->item_id;
                $favorite->save();
                return 1;
            } else {
                return 0;
            }            
        } catch (\Exception $e){
            return response()->json(['status'=>0,'message'=>'Something went wrong'],200);
        }
    }

    public function unfavorite(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }
        if($request->item_id == ""){
            return response()->json(["status"=>0,"message"=>"Item is required"],400);
        }

        $unfavorite=Favorite::where('user_id', $request->user_id)->where('item_id', $request->item_id)->delete();
        if ($unfavorite) {
            return 1;
        } else {
            return 0;
        }
    }
}
