<?php



namespace App\Http\Controllers\front;



use App\Http\Controllers\Controller;

use Illuminate\Http\Request;

use App\TermsCondition;

use App\Category;

use App\Cart;

use App\About;

use App\User;

use Validator;

use Auth;



class TermsController extends Controller

{



    public function terms()

    {
        $user_id  = @Auth::user()->id;
        $getcategory = Category::where('is_available','=','1')->where('is_deleted','=','2')->get();

        $cart=Cart::with('itemimage')->select('cart.id','cart.qty','cart.weight','cart.price','cart.variation_id','item.item_name','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$user_id)->get();

        $getdata=User::select('max_order_qty','min_order_amount','max_order_amount','currency','firebase','map','referral_amount')->where('type','1')
            ->get()->first();

        $getabout = About::where('id','=','1')->first();

        $gettermscondition = TermsCondition::where('id','1')->first();

        return view('front.terms',compact('getcategory','cart','gettermscondition','getabout','getdata'));

    }

}

