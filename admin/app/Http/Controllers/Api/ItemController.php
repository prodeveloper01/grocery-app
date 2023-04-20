<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Item;
use App\Favorite;
use App\User;
use App\ItemImages;
use App\Cart;
use Illuminate\Support\Facades\DB;
use Validator;

class ItemController extends Controller
{
    public function item(Request $request)
    {
        if($request->cat_id == ""){
            return response()->json(["status"=>0,"message"=>"category is required"],400);
        }

        if($request->user_id == ""){
            $user_id  = $request->user_id;
            $itemdata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
            ->leftJoin('favorite', function($query) use($user_id) {
                $query->on('favorite.item_id','=','item.id')
                ->where('favorite.user_id', '=', $user_id);
            })
            ->join('categories','item.cat_id','=','categories.id')
            ->where('item.item_status', '1')
            ->where('item.is_deleted','2')
            ->where('item.cat_id',$request['cat_id'])->orderBy('item.id', 'DESC')->paginate(10);

            if(!empty($itemdata))
            {
                return response()->json(['status'=>1,'message'=>'Item Successful','data'=>$itemdata],200);
            }
            else
            {
                return response()->json(['status'=>0,'message'=>'No data found'],200);
            }
            return response()->json(["status"=>0,"message"=>"category is required"],400);
        } else {
            $checkuser=User::where('id',$request->user_id)->first();

            if($checkuser->is_available == '1') 
            {
                $user_id  = $request->user_id;
                $itemdata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
                ->leftJoin('favorite', function($query) use($user_id) {
                    $query->on('favorite.item_id','=','item.id')
                    ->where('favorite.user_id', '=', $user_id);
                })
                ->join('categories','item.cat_id','=','categories.id')
                ->where('item.item_status', '1')
                ->where('item.is_deleted','2')
                ->where('item.cat_id',$request['cat_id'])->orderBy('item.id', 'DESC')->paginate(10);

                if(!empty($itemdata))
                {
                    return response()->json(['status'=>1,'message'=>'Item Successful','data'=>$itemdata],200);
                }
                else
                {
                    return response()->json(['status'=>0,'message'=>'No data found'],200);
                }
                return response()->json(["status"=>0,"message"=>"category is required"],400);
            } else {
                $status=2;
                $message='Your account has been blocked by Admin';
                return response()->json(['status'=>$status,'message'=>$message],422);
            }
        }
    }

    public function relateditem(Request $request)
    {
        if($request->cat_id == ""){
            return response()->json(["status"=>0,"message"=>"category is required"],400);
        }

        if($request->item_id == ""){
            return response()->json(["status"=>0,"message"=>"category is required"],400);
        }

        $getcategory = Item::where('id','=',$request->item_id)->first();

        if($request->user_id == ""){
            $user_id  = $request->user_id;
            
            $relatedproduct = Item::with(['category','itemimage','variation'])->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
            ->leftJoin('favorite', function($query) use($user_id) {
                $query->on('favorite.item_id','=','item.id')
                ->where('favorite.user_id', '=', $user_id);
            })->where('cat_id','=',$getcategory->cat_id)->where('item.id','!=',$request->item_id)->orderBy('id', 'DESC')->paginate(10);;

            if(!empty($relatedproduct))
            {
                return response()->json(['status'=>1,'message'=>'Item Successful','data'=>$relatedproduct],200);
            }
            else
            {
                return response()->json(['status'=>0,'message'=>'No data found'],200);
            }
            return response()->json(["status"=>0,"message"=>"category is required"],400);
        } else {
            $checkuser=User::where('id',$request->user_id)->first();

            if($checkuser->is_available == '1') 
            {
                $user_id  = $request->user_id;
                $relatedproduct = Item::with(['category','itemimage','variation'])->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
                ->leftJoin('favorite', function($query) use($user_id) {
                    $query->on('favorite.item_id','=','item.id')
                    ->where('favorite.user_id', '=', $user_id);
                })->where('cat_id','=',$getcategory->cat_id)->where('item.id','!=',$request->item_id)->orderBy('id', 'DESC')->paginate(10);;

                if(!empty($relatedproduct))
                {
                    return response()->json(['status'=>1,'message'=>'Item Successful','data'=>$relatedproduct],200);
                }
                else
                {
                    return response()->json(['status'=>0,'message'=>'No data found'],200);
                }
                return response()->json(["status"=>0,"message"=>"category is required"],400);
            } else {
                $status=2;
                $message='Your account has been blocked by Admin';
                return response()->json(['status'=>$status,'message'=>$message],422);
            }
        }
    }

    public function exploreitem(Request $request)
    {
        if ($request->user_id != "") {
            $checkuser=User::where('id',$request->user_id)->first();

            if($checkuser->is_available == '1') 
            {
                $user_id  = $request->user_id;

                $exploredata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
                ->leftJoin('favorite', function($query) use($user_id) {
                    $query->on('favorite.item_id','=','item.id')
                    ->where('favorite.user_id', '=', $user_id);
                })
                ->join('categories','item.cat_id','=','categories.id')
                ->where('categories.is_available','1')
                ->where('item.item_status', '1')
                ->where('item.is_deleted','2')
                ->inRandomOrder('1234')->paginate(10);

                if(!empty($exploredata))
                {
                    return response()->json(['status'=>1,'message'=>'Explore list Successful','data'=>$exploredata],200);
                }
                else
                {
                    return response()->json(['status'=>0,'message'=>'No data found'],200);
                }
            } else {
                $status=2;
                $message='Your account has been blocked by Admin';
                return response()->json(['status'=>$status,'message'=>$message],422);
            }
        } else {
            $user_id  =  $request->user_id;
            $exploredata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
                ->leftJoin('favorite', function($query) use($user_id) {
                    $query->on('favorite.item_id','=','item.id')
                    ->where('favorite.user_id', '=', $user_id);
                })
                ->join('categories','item.cat_id','=','categories.id')
                ->where('categories.is_available','1')
                ->where('item.item_status', '1')
                ->where('item.is_deleted','2')
                ->inRandomOrder()->paginate(10);

            if(!empty($exploredata))
            {
                return response()->json(['status'=>1,'message'=>'Explore list Successful','data'=>$exploredata],200);
            }
            else
            {
                return response()->json(['status'=>0,'message'=>'No data found'],200);
            }
        }
    }

    public function latestitem(Request $request)
    {
        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();
        if ($request->user_id != "") {
            $checkuser=User::where('id',$request->user_id)->first();

            if($checkuser->is_available == '1') 
            {
                $user_id  = $request->user_id;
                $itemdata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
                ->leftJoin('favorite', function($query) use($user_id) {
                    $query->on('favorite.item_id','=','item.id')
                    ->where('favorite.user_id', '=', $user_id);
                })
                ->join('categories','item.cat_id','=','categories.id')
                ->where('item.item_status', '1')
                ->where('item.is_deleted','2')
                ->where('categories.is_available','1')
                ->orderBy('item.id', 'DESC')->paginate(10);

                if(!empty($itemdata))
                {
                    return response()->json(['status'=>1,'message'=>'Latest item Successful','data'=>$itemdata,'currency'=>$getdata->currency,'max_order_qty'=>$getdata->max_order_qty,'min_order_amount'=>$getdata->min_order_amount,'max_order_amount'=>$getdata->max_order_amount,'map'=>$getdata->map,'referral_amount'=>$getdata->referral_amount],200);
                }
                else
                {
                    return response()->json(['status'=>0,'message'=>'No data found'],200);
                }
            } else {
                $status=2;
                $message='Your account has been blocked by Admin';
                return response()->json(['status'=>$status,'message'=>$message],422);
            }
        } else {
            $user_id  = $request->user_id;
            $itemdata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
            ->leftJoin('favorite', function($query) use($user_id) {
                $query->on('favorite.item_id','=','item.id')
                ->where('favorite.user_id', '=', $user_id);
            })
            ->join('categories','item.cat_id','=','categories.id')
            ->where('item.item_status', '1')
            ->where('item.is_deleted','2')
            ->where('categories.is_available','1')
            ->orderBy('item.id', 'DESC')->paginate(10);

            if(!empty($itemdata))
            {
                return response()->json(['status'=>1,'message'=>'Latest item Successful','data'=>$itemdata,'currency'=>$getdata->currency,'max_order_qty'=>$getdata->max_order_qty,'min_order_amount'=>$getdata->min_order_amount,'max_order_amount'=>$getdata->max_order_amount,'map'=>$getdata->map],200);
            }
            else
            {
                return response()->json(['status'=>0,'message'=>'No data found'],200);
            }
        }
    }

    public function itemdetails(Request $request)
    {
        if($request->item_id == ""){
            return response()->json(["status"=>0,"message"=>"Item ID is required"],400);
        }

    	$itemdata=Item::with('itemimagedetails')->with('variation')->select('item.id','item.cat_id','item.item_name','item.item_description','item.brand','item.manufacturer','item.country_origin','item.ingredient_type','item.delivery_time','categories.category_name')
    	->join('categories','item.cat_id','=','categories.id')
    	->where('item.id',$request['item_id'])->get()->first();

        $cartdata=Cart::where('user_id',$request->user_id)->get();

        $checkcart=Cart::where('cart.user_id',$request->user_id)
        ->where('cart.item_id', $request->item_id)
        ->get()
        ->first();

        if($checkcart!="") {
            $cart = "1";
        } else {
            $cart = "2";
        }
        
        if($cartdata!="") {
            $cartcount = $cartdata->count('id');
        } else {
            $cartcount = "";
        }
        
        if($cartdata->sum('price')!="") {
            $cartprice = $cartdata->sum('price');
        } else {
            $cartprice = "";
        }

        if($itemdata->brand!="") {
            $brand = $itemdata->brand;
        } else {
            $brand = "-";
        }

        if($itemdata->manufacturer!="") {
            $manufacturer = $itemdata->manufacturer;
        } else {
            $manufacturer = "-";
        }

        if($itemdata->country_origin!="") {
            $country_origin = $itemdata->country_origin;
        } else {
            $country_origin = "-";
        }

        if($itemdata->ingredient_type!="") {
            $ingredient_type = $itemdata->ingredient_type;
        } else {
            $ingredient_type = "-";
        }
        $data = array(
            'id' => $itemdata->id,
            'cat_id' => $itemdata->cat_id,
            'item_name' => $itemdata->item_name,
            'item_description' => $itemdata->item_description,
            'brand' => $brand,
            'manufacturer' => $manufacturer,
            'country_origin' => $country_origin,
            'ingredient_type' => $ingredient_type,
            'delivery_time' => $itemdata->delivery_time,
            'category_name' => $itemdata->category_name,
            'images' => $itemdata->itemimagedetails,
            'variation' => $itemdata->variation,
            'is_on_cart' => $cart,             
            'cartcount' => "$cartcount",       
            'cartprice' => "$cartprice",
            
        ); 

        if(!empty($data))
        {
        	return response()->json(['status'=>1,'message'=>'Item Successful','data'=>$data],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }

    public function searchitem(Request $request)
    {

        if($request->keyword == ""){
            return response()->json(["status"=>0,"message"=>"Keyword is required"],400);
        }

        $user_id  = $request->user_id;
        $itemdata=Item::with('itemimage')->with('variation')->select('item.id','item.item_name',DB::raw('(case when favorite.item_id is null then 0 else 1 end) as is_favorite'))
        ->leftJoin('favorite', function($query) use($user_id) {
            $query->on('favorite.item_id','=','item.id')
            ->where('favorite.user_id', '=', $user_id);
        })
        ->join('categories','item.cat_id','=','categories.id')
        ->where('item.item_name', 'LIKE', '%' . $request['keyword'] . '%')
        ->where('item.item_status', '1')
        ->where('item.is_deleted', '2')
        ->where('categories.is_available','1')
        ->orderBy('item.id', 'DESC')->paginate(10);
        
        if(!$itemdata->isEmpty())
        {
            return response()->json(['status'=>1,'message'=>'Item Successful','data'=>$itemdata],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }

    public function addfavorite(Request $request)
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

            if($data=="") {
                $favorite = new Favorite;
                $favorite->user_id =$request->user_id;
                $favorite->item_id =$request->item_id;
                $favorite->save();

                return response()->json(['status'=>1,'message'=>'Item is added to your favourite list'],200);
            } else {
                return response()->json(['status'=>0,'message'=>'Item is already available in you favourite list.'],400);
            }
            
        } catch (\Exception $e){
            return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
        }
    }

    public function favoritelist(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }

        $favorite=Favorite::with('itemimage')->with('variation')->select('favorite.id as favorite_id','item.id','item.item_name')
        ->join('item','favorite.item_id','=','item.id')
        ->join('categories','item.cat_id','=','categories.id')
        ->where('item.item_status','1')
        ->where('categories.is_available','1')
        ->where('item.is_deleted','2')
        ->where('favorite.user_id',$request['user_id'])
        ->paginate(10); 

        if(!empty($favorite))
        {
            return response()->json(['status'=>1,'message'=>'Favorite List','data'=>$favorite],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }

    public function removefavorite(Request $request)
    {
        if($request->favorite_id == ""){
            return response()->json(["status"=>0,"message"=>"Favorite product ID is required"],400);
        }

        $favorite=Favorite::where('id', $request->favorite_id)->delete();

        if($favorite)
        {
            return response()->json(['status'=>1,'message'=>'Item is removed from favourite list'],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }
}