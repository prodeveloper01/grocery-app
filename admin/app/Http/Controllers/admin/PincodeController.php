<?php

namespace App\Http\Controllers\admin;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Pincode;
use Validator;
class PincodeController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $getpincode = Pincode::where('is_deleted','2')->get();
        return view('pincode',compact('getpincode'));
    }

    public function list()
    {
        $getpincode = Pincode::where('is_deleted','2')->get();
        return view('theme.pincodetable',compact('getpincode'));
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $s
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $validation = Validator::make($request->all(),[
          'pincode' => 'required'
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
            $pincode = new Pincode;
            $pincode->pincode =$request->pincode;
            $pincode->save();
            $success_output = 'Pincode Added Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show(Request $request)
    {
        $getpincode = Pincode::where('id',$request->id)->first();
        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Pincode fetch successfully', 'ResponseData' => $getpincode], 200);
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit(Request $req)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request)
    {

        $validation = Validator::make($request->all(),[
          'pincode' => 'required'
        ]);

        $error_array = array();
        $success_output = '';
        if ($validation->fails())
        {
            foreach($validation->messages()->getMessages() as $field_name => $messages)
            {
                $error_array[] = $messages;
            }
            // dd($error_array);
        }
        else
        {
            $pincode = new Pincode;
            $pincode->exists = true;
            $pincode->id = $request->id;
            $pincode->pincode =$request->pincode;
            $pincode->save();           

            $success_output = 'Pincode updated Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function status(Request $request)
    {
        $pincode = Pincode::where('id', $request->id)->update( array('is_available'=>$request->status) );
        if ($pincode) {
            return 1;
        } else {
            return 0;
        }
    }

    public function delete(Request $request)
    {
        $pincode = Pincode::where('id', $request->id)->update( array('is_deleted'=>'1') );
        if ($pincode) {
            return 1;
        } else {
            return 0;
        }
    }
}
