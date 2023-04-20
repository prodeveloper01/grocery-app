<?php
  
namespace App\Http\Controllers\Auth;
  
use App\Http\Controllers\Controller;
use Socialite;
use Auth;
use Exception;
use App\User;
use Storage;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Redirect;
use Session;
use App\Cart;
  
class GoogleController extends Controller
{
    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function redirectToGoogle()
    {
        return Socialite::driver('google')->redirect();
    }
      
    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function handleGoogleCallback()
    {
        try {
    
            $user = Socialite::driver('google')->user();

            $usergoogle=User::where('google_id',$user->id)->first();
            $checkuser=User::where('email','=',$user->email)->where('login_type','!=','google')->first();

            if (!empty($checkuser)) {
                return Redirect::to('/signin')->with('danger', 'Email id Already exist');
            }

            $otp = rand ( 100000 , 999999 );
            if ($usergoogle != "" OR @$usergoogle->email == $user->email AND $user->email != "") {
                if ($usergoogle->mobile == "") {
                    session ( [
                        'name' => $user->name,
                        'email' => $user->email,
                        'google_id' => $user->id,
                    ] );
                    return Redirect::to('/signup');
                } else {
                    
                    if($usergoogle->is_verified == '1') 
                    {
                        if($usergoogle->is_available == '1') {
                            // Check item in Cart
                            $cart=Cart::where('user_id',$usergoogle->id)->count();
                            $getdata=User::select('referral_amount')->where('type','1')->first();

                            $auth=Auth::login($usergoogle);

                            Storage::disk('local')->put("cart", $cart);

                            return Redirect::to('/');
                        } else {
                            return Redirect::to('/signin')->with('danger', 'Your account has been blocked by Admin');
                        }
                    } else {
                                        
                        $title='Email Verification Code';
                        $email=$usergoogle->email;
                        $data=['title'=>$title,'email'=>$email,'otp'=>$otp];

                        Mail::send('Email.emailverification',$data,function($message)use($data){
                            $message->from(env('MAIL_USERNAME'))->subject($data['title']);
                            $message->to($data['email']);
                        } );

                        $otp_data['otp'] = $otp;
                        $update=User::where('email',$usergoogle->email)->update($otp_data);

                        if (env('Environment') == 'sendbox') {
                            session ( [
                                'email' => $usergoogle->email,
                                'otp' => $otp,
                            ] );
                        } else {
                            session ( [
                                'email' => $usergoogle->email,
                            ] );
                        }
                        return Redirect::to('/email-verify')->with('success', 'Email has been sent to your registered email address'); 
                    }
                }
            } else {

                $res = new User;
                $res->name =$user->name;
                $res->email =$user->email;
                $res->profile_image ='unknown.png';
                $res->login_type ='google';
                $res->google_id =$user->id;
                $res->type ='2';
                $res->save();

                session ( [
                    'name' => $user->name,
                    'email' => $user->email,
                    'google_id' => $user->id,
                ] );
                return Redirect::to('/signup');

            }
    
        } catch (Exception $e) {
            return Redirect::to('/signin')->with('danger', 'Something went wrong');
        }
    }
}