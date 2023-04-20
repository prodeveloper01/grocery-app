<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Cart;
use App\Item;
use App\ItemImages;
use App\Variation;
use App\User;
use Illuminate\Support\Facades\DB;
use Validator;

class CartController extends Controller
{
    
      public function cart(Request $request)
      {
        if($request->item_id == ""){
            return response()->json(["status"=>0,"message"=>"Item is required"],400);
        }
        if($request->qty == ""){
            return response()->json(["status"=>0,"message"=>"Qty is required"],400);
        }
        if($request->price == ""){
            return response()->json(["status"=>0,"message"=>"Price is required"],400);
        }
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User ID is required"],400);
        }

        $data=Cart::where('cart.user_id',$request['user_id'])
                ->where('cart.item_id', $request['item_id'])
                ->where('cart.weight', $request['weight'])
                ->get()
                ->first();
        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount')->where('type','1')
        ->get()->first();

        $checkitmqty=Variation::where('id',$request->variation_id)->first();

        if(empty($checkitmqty)) {
          return response()->json(['status'=>3,'message'=>"Please Refresh"],200);
        }

        $getqty = @$data['qty']+$request['qty'];

        if ($checkitmqty->stock < $getqty) {

          return response()->json(['status'=>0,'message'=>"Only ".$checkitmqty->stock." units are available for this item"],200);

        } else{

          try {
            if($data!="") {
              if (@$data->weight == $request['weight'] && @$data->item_id == $request['item_id']) {

                if ($getdata->max_order_qty < $getqty) {
                  return response()->json(['status'=>0,'message'=>"You've reached the maximum units allowed for the purchase of this item"],200);
                }

                $result = DB::table('cart')
                ->where('cart.user_id',$data['user_id'])
                ->where('cart.item_id', $data['item_id'])
                ->where('cart.id', $data['id'])
                ->update([
                    'qty' => $data['qty']+$request['qty'],
                ]);

                return response()->json(['status'=>1,'message'=>'Quantity is updated'],200);

              } else {
                  $cart = new Cart;
                  $cart->item_id =$request->item_id;
                  $cart->item_name =$request->item_name;
                  $cart->image_name =$request->image_name;
                  $cart->qty =$request->qty;
                  $cart->price =$request->price;
                  $cart->weight =$request->weight;
                  $cart->user_id =$request->user_id;
                  $cart->variation_id =$request->variation_id;
                  $cart->save();

                  return response()->json(['status'=>1,'message'=>'Item is added to your cart'],200);
              }
            } else {
                $cart = new Cart;
                $cart->item_id =$request->item_id;
                $cart->item_name =$request->item_name;
                $cart->image_name =$request->image_name;
                $cart->qty =$request->qty;
                $cart->price =$request->price;
                $cart->weight =$request->weight;
                $cart->user_id =$request->user_id;
                $cart->variation_id =$request->variation_id;
                $cart->save();

                return response()->json(['status'=>1,'message'=>'Item is added to your cart'],200);
            }

          } catch (\Exception $e){

              return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
          }
          
        }
        return response()->json(['status'=>3,'message'=>"Please Refresh"],200);

      }

   	public function getcart(Request $request)
   	{
   	    if($request->user_id == ""){
   	        return response()->json(["status"=>0,"message"=>"User ID is required"],400);
   	    }

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$request->user_id)->get();

        foreach ($cart as $value) {

          $getcart=Variation::where('id',$value['variation_id'])->get();

            foreach ($getcart as $vvalue) {

            if ($vvalue['stock'] == "0") {
              $cart=Cart::where('variation_id',$value['variation_id'])->where('user_id', $request->user_id)->delete();
              
            } elseif ($value['qty'] > $vvalue['stock']) {
              DB::table('cart')->where('variation_id',$value['variation_id'])->where('user_id',$request->user_id)->update([ 'qty' => $vvalue['stock']]);
            }

          }
        }

        $cartdata=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$request->user_id)->get();
   	    

   	    if(!empty($cartdata))
   	    {
   	        return response()->json(['status'=>1,'message'=>'Cart Data Successful','data'=>$cartdata],200);
   	    }
   	    else
   	    {
   	        return response()->json(['status'=>0,'message'=>'No data found'],200);
   	    }
   	}

   	public function qtyupdate(Request $request)
   	{
   	    if($request->cart_id == ""){
   	        return response()->json(["status"=>0,"message"=>"Cart ID is required"],400);
   	    }
   	    if($request->item_id == ""){
   	        return response()->json(["status"=>0,"message"=>"Item is required"],400);
   	    }
   	    if($request->qty == ""){
   	        return response()->json(["status"=>0,"message"=>"Qty is required"],400);
   	    }
   	    if($request->user_id == ""){   	        
          return response()->json(["status"=>0,"message"=>"User ID is required"],400);
   	    }

        $checkitmqty=Variation::where('id',$request->variation_id)->first();

        if ($request->qty <= $checkitmqty->stock) {
          $cart = new Cart;
          $cart->exists = true;
          $cart->id = $request->cart_id;
          $cart->item_id =$request->item_id;
    	    $cart->qty =$request->qty;
    	    $cart->user_id =$request->user_id;
          $cart->save();
          return response()->json(['status'=>1,'message'=>'Quantity is updated'],200);
        } else {
          return response()->json(['status'=>0,'message'=>"Only ".$checkitmqty->stock." units are available for this item"],200);
        }
   	}

    public function deletecartitem(Request $request)
    {
        if($request->cart_id == ""){
            return response()->json(["status"=>0,"message"=>"Cart data is required"],400);
        }

        $cart=Cart::where('id', $request->cart_id)->delete();
        if($cart)
        {
            return response()->json(['status'=>1,'message'=>'Item is deleted'],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'Somethig went wrong'],200);
        }
    }

    public function cartcount(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User ID is required"],400);
        }

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$request->user_id)->get();

        foreach ($cart as $value) {

          $getcart=Variation::where('id',$value['variation_id'])->get();

            foreach ($getcart as $vvalue) {

            if ($vvalue['stock'] == "0") {
              $cart=Cart::where('variation_id',$value['variation_id'])->where('user_id', $request->user_id)->delete();
              
            } elseif ($value['qty'] > $vvalue['stock']) {
              DB::table('cart')->where('variation_id',$value['variation_id'])->where('user_id',$request->user_id)->update([ 'qty' => $vvalue['stock']]);
            }

          }
        }

        $cartdata=Cart::where('user_id',$request->user_id)->where('is_available','=','1')->get();

        if($cartdata!="") {
            $cartcount = $cartdata->count('id');
        } else {
            $cartcount = "";
        }

        $total = 0;
        foreach ($cartdata as $value) {

          $total = $total+$value->qty*$value->price;
        }

        $data = array(             
            'cartcount' => "$cartcount",       
            'cartprice' => "$total",            
        ); 

        if(!empty($cartdata))
        {
            return response()->json(['status'=>1,'message'=>'Cart Data Successful','data'=>$data],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }
}