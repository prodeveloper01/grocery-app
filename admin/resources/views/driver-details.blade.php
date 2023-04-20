@extends('theme.default')

@section('content')
<!-- row -->

<div class="row page-titles mx-0">
    <div class="col p-md-0">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="{{URL::to('/admin/home')}}">Dashboard</a></li>
            <li class="breadcrumb-item active"><a href="javascript:void(0)">User Details</a></li>
        </ol>
    </div>
</div>
<!-- row -->

<div class="container-fluid">
    <!-- End Row -->

    <div class="row">
        <div class="col-lg-3 col-sm-6">
            <div class="card">
                <div class="card-body">
                    <div class="text-center">
                        <img src='{!! asset("public/images/profile/".$getusers->profile_image) !!}' width="100px" class="rounded-circle" alt="">
                        <h5 class="mt-3 mb-1">{{$getusers->name}}</h5>
                        <p class="m-0">{{$getusers->email}}</p>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-sm-6">
            <div class="card">
                <div class="card-body">
                    <div class="text-center">
                        <img src='{!! asset("public/assets/images/shopping-cart.png") !!}' width="100px" alt="">
                        <h5 class="mt-3 mb-1">{{count($getorders)}}</h5>
                        <p class="m-0">Total Order</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-3 col-sm-6">
            <div class="card">
                <div class="card-body">
                    <div class="text-center">
                        <img src='{!! asset("public/assets/images/check-list.png") !!}' width="100px" alt="">
                        <h5 class="mt-3 mb-1">{{$ongoingorder}}</h5>
                        <p class="m-0">Total Ongoing Order</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-3 col-sm-6">
            <div class="card">
                <div class="card-body">
                    <div class="text-center">
                        <img src='{!! asset("public/assets/images/shopping-bag.png") !!}' width="100px" alt="">
                        <h5 class="mt-3 mb-1">{{$completedorders}}</h5>
                        <p class="m-0">Total Completed Order</p>
                    </div>
                </div>
            </div>
        </div>

    </div>


    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-body">
                    <h4 class="card-title">All Ordres</h4>
                    <div class="table-responsive" id="table-display">
                        <table class="table table-striped table-bordered zero-configuration">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Order Number</th>
                                    <th>Address</th>
                                    <th>Payment Type</th>
                                    <th>Payment ID</th>
                                    <th>Order Type</th>
                                    <th>Order Status</th>
                                    <th>Order Assigned To</th>
                                    <th>Created at</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php
                                $i = 1;
                                foreach ($getorders as $orders) {
                                ?>
                                <tr id="dataid{{$orders->id}}">
                                    <td>{{$i}}</td>
                                    <td>{{$orders->order_number}}</td>
                                    <td>{{$orders->address}}</td>
                                    <td>
                                        @if($orders->payment_type =='0')
                                            COD
                                        @elseif($orders->payment_type =='1')
                                            RazorPay
                                        @elseif($orders->payment_type =='2')
                                            Stripe
                                        @elseif($orders->payment_type =='3')
                                            Wallet
                                        @endif
                                    </td>
                                    <td>
                                        @if($orders->razorpay_payment_id == '')
                                            --
                                        @else
                                            {{$orders->razorpay_payment_id}}
                                        @endif
                                    </td>
                                    <td>
                                        @if($orders->order_type == 1)
                                            Delivery
                                        @else
                                            Pickup
                                        @endif
                                    </td>
                                    <td>
                                        @if($orders->status == '1')
                                            Order Received
                                        @elseif ($orders->status == '2')
                                            On the way
                                        @elseif ($orders->status == '3')
                                            Assigned to Driver
                                        @elseif ($orders->status == '4')
                                            Delivered
                                        @elseif ($orders->status == '5')
                                            Cancelled by User
                                        @elseif ($orders->status == '6')
                                            Cancelled by Admin
                                        @endif
                                    </td>
                                    <td>
                                        @if ($orders->name == "")
                                            --
                                        @else
                                            {{$orders->name}}
                                        @endif
                                    </td>
                                    <td>{{$orders->created_at}}</td>
                                    <td>
                                        <span>
                                            <a data-toggle="tooltip" href="{{URL::to('admin/invoice/'.$orders->id)}}" data-original-title="View">
                                                <span class="badge badge-warning">View</span>
                                            </a>
                                        </span>
                                    </td>
                                </tr>
                                <?php
                                $i++;
                                }
                                ?>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- #/ container -->

@endsection
@section('script')
@endsection