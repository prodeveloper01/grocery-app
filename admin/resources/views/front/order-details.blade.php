@include('front.theme.header')



<!-- =========================== Breadcrumbs =================================== -->

<div class="breadcrumbs_wrap dark">

    <div class="container">

        <div class="row align-items-center">

            

            <div class="col-lg-12 col-md-12 col-sm-12 col-12">

                <div class="text-center">

                    <h2 class="breadcrumbs_title">Order Details</h2>

                    <nav aria-label="breadcrumb">

                      <ol class="breadcrumb">

                        <li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>

                        <li class="breadcrumb-item active" aria-current="page">Order Details</li>

                      </ol>

                    </nav>

                </div>

            </div>

            

        </div>

    </div>

</div>

<!-- =========================== Breadcrumbs =================================== -->



<!-- =========================== My Order =================================== -->

<section class="gray">

    <div class="container">

        <div class="row">

            

            @include('front.theme.sidebar')

            

            <div class="col-lg-8 col-md-9">

            

                <div class="card-body bg-white mb-4">

                    <div class="row">

                        <div class="col-6 col-lg-3">

                            <!-- Heading -->

                            <h6 class="text-muted mb-1">Order No:</h6>

                            <!-- Text -->

                            <p class="mb-lg-0 font-size-sm font-weight-bold">{{$summery['order_number']}}</p>

                        </div>

                        

                        <div class="col-6 col-lg-3">

                            <!-- Heading -->

                            <h6 class="text-muted mb-1">Order date:</h6>

                            <!-- Text -->

                            <p class="mb-lg-0 font-size-sm font-weight-bold">

                                <span>{{$summery['created_at']}}</span>

                            </p>

                        </div>

                        

                        <div class="col-6 col-lg-3">

                            <!-- Heading -->

                            <h6 class="text-muted mb-1">Status:</h6>

                            <!-- Text -->

                            <p class="mb-0 font-size-sm font-weight-bold">

                                @if ($summery['status'] == 1)

                                    Order Placed

                                @elseif($summery['status'] == 2)

                                    Order ready

                                @elseif($summery['status'] == 3)

                                    Order on the way

                                @elseif($summery['status'] == 4)

                                    Order delivered

                                @elseif($summery['status'] == 5)

                                    Order Cancelled by You

                                @elseif($summery['status'] == 6)

                                    Order Cancelled by Admin

                                @endif

                            </p>

                        </div>

                        

                        <div class="col-6 col-lg-3">

                            <!-- Heading -->

                            <h6 class="text-muted mb-1">Order Amount:</h6>

                            <!-- Text -->

                            <p class="mb-0 font-size-sm font-weight-bold">{{$getdata->currency}}{{number_format($summery['order_total'], 2)}}</p>

                        </div>

                        

                    </div>

                </div>



                @if($summery['driver_name'] != "")

                    <div class="card-body bg-white mb-4">
                        <h4>Delivery boy Information</h4>
                        <div class="row mt-2">

                            <div class="col-6 col-lg-3">

                                <!-- Heading -->

                                <h6 class="text-muted mb-1"></h6>

                                <!-- Text -->

                                <p class="mb-lg-0 font-size-sm font-weight-bold"><img src='{{$summery["driver_profile_image"]}}' width="50px" alt=""></p>

                            </div>



                            <div class="col-6 col-lg-3">

                                <!-- Heading -->

                                <h6 class="text-muted mb-1">Name:</h6>

                                <!-- Text -->

                                <p class="mb-lg-0 font-size-sm font-weight-bold">

                                    <span>{{$summery['driver_name']}}</span>

                                </p>

                            </div>

                            

                            <div class="col-6 col-lg-3">

                                <!-- Heading -->

                                <h6 class="text-muted mb-1">Mobile Number:</h6>

                                <!-- Text -->

                                <p class="mb-lg-0 font-size-sm font-weight-bold">

                                    <span>{{$summery['driver_mobile']}}</span>

                                </p>

                            </div>

                            

                        </div>

                    </div>

                @endif

                

                <!-- Order Items -->

                <div class="card style-2 mb-4">

                    <div class="card-header">

                        <h4 class="mb-0">Order Item {{count($orderdata)}}</h4>

                    </div>

                    <div class="card-body">

                        <ul class="item-groups">

                            @foreach($orderdata as $row)

                            <!-- Single Items -->

                            <div class="cart_select_items">

                                <!-- Single Item -->

                                <div class="product cart_selected_single">

                                    <div class="cart_selected_single_thumb">

                                        <a href="#"><img src='{{$row["itemimage"]->image }}' alt="..." class="img-fluid"></a>

                                    </div>

                                    <div class="cart_selected_single_caption">

                                        <h4 class="product_title"><a class="text-body" href="{{URL::to('product-details/'.$row->slug)}}">{{$row->item_name}}</a></h4>

                                        <span class="numberof_item mt-2">

                                            <b>{{$row->weight}}</b>

                                            <br>

                                            {{$row->qty}} * {{$getdata->currency}}{{number_format($row->total_price, 2)}}

                                        </span>

                                    </div>

                                </div>

                            </div>

                            <?php

                                $data[] = array(

                                    "total_price" => $row->total_price*$row->qty

                                );

                            ?>

                            @endforeach

                        </ul>

                    </div>

                </div>

                

                @if ($summery['status'] == 1)

                <div class="mb-4">

                    <button type="button" class="btn btn-block btn-dark mb-2" onclick="OrderCancel('{{$summery['id']}}')">Cancel order</button>

                </div>

                @endif



                <!-- Total Items -->

                <div class="card style-2 mb-4">

                    <div class="card-header">

                        <h4 class="mb-0">Price Summary</h4>

                    </div>

                    <div class="card-body">

                        <ul class="list-group list-group-sm list-group-flush-y list-group-flush-x">

                            <li class="list-group-item d-flex">

                                <span>Subtotal</span>

                                <span class="ml-auto">{{$getdata->currency}}{{number_format(array_sum(array_column(@$data, 'total_price')), 2)}}</span>

                            </li>

                        

                            <li class="list-group-item d-flex">

                                <span>Tax</span>

                                <span class="ml-auto">{{$getdata->currency}}{{number_format($summery['tax_amount'], 2)}}</span>

                            </li>



                            <li class="list-group-item d-flex">

                                <span>Delivery Charge</span>

                                <span class="ml-auto">{{$getdata->currency}}{{number_format($summery['delivery_charge'], 2)}}</span>

                            </li>

                            

                            <li class="list-group-item d-flex font-size-lg font-weight-bold">

                                <span>Total</span>

                                <span class="ml-auto">{{$getdata->currency}}{{number_format($summery['order_total'], 2)}}</span>

                            </li>

                        </ul>

                    </div>

                </div>

                

                <!-- Shipping & Billing -->

                <div class="card style-2">

                    <div class="card-header">

                        <h4 class="mb-0">Shipping & Billing  Details</h4>

                    </div>

                    <div class="card-body">

                        <div class="row">

                            <div class="col-12 col-md-4">

                                <!-- Heading -->

                                <p class="mb-2 font-weight-bold">

                                  Delivery Address:

                                </p>



                                @if($summery['order_type'] == 1)



                                    <p class="mb-7 mb-md-0">

                                      {{$summery['address']}}<br>

                                      {{$summery['building']}}<br>

                                      {{$summery['landmark']}}<br>

                                      {{$summery['pincode']}}<br>

                                    </p>

                                @else

                                    <p class="mb-7 mb-md-0">

                                        Pickup Order

                                    </p>



                                @endif



                            </div>

                          

                            <div class="col-12 col-md-4">

                                

                            </div>

                            

                            <div class="col-12 col-md-4">



                                <!-- Heading -->

                                <p class="mb-2 font-weight-bold">

                                  Order Notes:

                                </p>



                                @if($summery['order_notes'] != "")

                                    <p class="mb-4 text-gray-500">

                                      {{$summery['order_notes']}}

                                    </p>

                                @else

                                    <p class="mb-4 text-gray-500">

                                        ---

                                    </p>

                                @endif



                                <!-- Heading -->

                                <p class="mb-2 font-weight-bold">

                                  Payment Method:

                                </p>



                                <p class="mb-0">

                                  @if($summery['payment_type'] == 1)

                                    Razorpay Payment

                                  @elseif($summery['payment_type'] == 2)

                                    Stripe Payment

                                  @elseif($summery['payment_type'] == 3)

                                    Wallet Payment

                                  @else

                                    Cash Payment

                                  @endif

                                </p>



                            </div>

                        </div>

                    </div>

                </div>

                

            </div>

            

        </div>

    </div>

</section>

<!-- =========================== My Order =================================== -->

<!-- ======================== Fresh Vegetables & Fruits End ==================== -->

@include('front.theme.footer')