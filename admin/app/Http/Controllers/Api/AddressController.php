<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Address;
use App\Pincode;
use Validator;

class AddressController extends Controller
{
    public function address(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }
        if($request->full_name == ""){
            return response()->json(["status"=>0,"message"=>"Full name is required"],400);
        }
        if($request->address_type == ""){
            return response()->json(["status"=>0,"message"=>"Address Type is required"],400);
        }
        if($request->address == ""){
            return response()->json(["status"=>0,"message"=>"Address is required"],400);
        }
        if($request->lat == ""){
            return response()->json(["status"=>0,"message"=>"Lat is required"],400);
        }
        if($request->lang == ""){
            return response()->json(["status"=>0,"message"=>"Longitude is required"],400);
        }
        if($request->landmark == ""){
            return response()->json(["status"=>0,"message"=>"landmark is required"],400);
        }
        if($request->building == ""){
            return response()->json(["status"=>0,"message"=>"Building is required"],400);
        }
        if($request->mobile == ""){
            return response()->json(["status"=>0,"message"=>"Mobile is required"],400);
        }
        if($request->pincode == ""){
            return response()->json(["status"=>0,"message"=>"Pincode is required"],400);
        }

        $pincode=Pincode::select('pincode')->where('pincode',$request['pincode'])->where('is_available','1')->where('is_deleted','2')->first();

        if($pincode['pincode']== $request->pincode) {
            if(!empty($pincode))
            {
                try {
                    $address = new Address;
                    $address->user_id =$request->user_id;
                    $address->full_name =$request->full_name;
                    $address->address_type =$request->address_type;
                    $address->address =$request->address;
                    $address->lat =$request->lat;
                    $address->lang =$request->lang;
                    $address->landmark =$request->landmark;
                    $address->building =$request->building;
                    $address->mobile =$request->mobile;
                    $address->pincode =$request->pincode;
                    $address->save();

                    return response()->json(['status'=>1,'message'=>'Address saved'],200);
                } catch (\Exception $e){
                    return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
                }
            }
        } else {
            return response()->json(['status'=>0,'message'=>'Delivery is not available in your area (Pincode)'],200);
        }
    }

    public function getaddress(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }

        try {
            $address=Address::select('id','user_id','full_name','address_type','address','lat','lang','landmark','building','mobile','pincode')->where('user_id',$request->user_id)->get();

            return response()->json(['status'=>1,'message'=>'Address','data'=>$address],200);
        } catch (\Exception $e){
            return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
        }
    }

    public function updateaddress(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }

        if($request->address_id == ""){
            return response()->json(["status"=>0,"message"=>"Address is required"],400);
        }

        $pincode=Pincode::select('pincode')->where('pincode',$request['pincode'])->where('is_available','1')->where('is_deleted','2')->first();

        if($pincode['pincode']== $request->pincode) {
            if(!empty($pincode))
            {
                try {
                    $data_address['full_name'] = $request->full_name;
                    $data_address['address_type'] = $request->address_type;
                    $data_address['address'] = $request->address;
                    $data_address['lat'] = $request->lat;
                    $data_address['lang'] = $request->lang;
                    $data_address['landmark'] = $request->landmark;
                    $data_address['building'] = $request->building;
                    $data_address['mobile'] = $request->mobile;
                    $data_address['pincode'] = $request->pincode;

                    $update=Address::where('user_id',$request->user_id)->where('id',$request->address_id)->update($data_address);

                    return response()->json(['status'=>1,'message'=>'Address updated'],200);
                } catch (\Exception $e){
                    return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
                }
            }
        } else {
            return response()->json(['status'=>0,'message'=>'Delivery is not available in your area (Pincode)'],200);
        }
    }

    public function deleteaddress(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }

        if($request->address_id == ""){
            return response()->json(["status"=>0,"message"=>"Address is required"],400);
        }

        try {
            $delete=Address::where('user_id',$request->user_id)->where('id', $request->address_id)->delete();

            return response()->json(['status'=>1,'message'=>'Address deleted'],200);
        } catch (\Exception $e){
            return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
        }
    }
}
