@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
    <div class="container">
        <div class="row align-items-center">
            
            <div class="col-lg-12 col-md-12 col-sm-12 col-12">
                <div class="text-center">
                    <h2 class="breadcrumbs_title">Track order: {{$status->order_number}}</h2>
                    <nav aria-label="breadcrumb">
                      <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
                        <li class="breadcrumb-item active" aria-current="page">Track Order</li>
                      </ol>
                    </nav>
                </div>
            </div>
            
        </div>
    </div>
</div>
<!-- =========================== Breadcrumbs =================================== -->

<!-- =========================== My All Orders =================================== -->
<section class="gray">
    <div class="container">
        <div class="row">
        
            @include('front.theme.sidebar')
            
            <div class="col-lg-8 col-md-9 col-sm-12 col-12">
                <div class="checked-shop">
                
                    <div class="row">
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <div class="ship_status_box"><span class="text-bold mr-2">Status:</span>
                                @if ($status['status'] == 1)
                                    Order Placed
                                @elseif($status['status'] == 2)
                                    Order ready
                                @elseif($status['status'] == 3)
                                    Order on the way
                                @elseif($status['status'] == 4)
                                    Order delivered
                                @elseif($status['status'] == 5)
                                    Order Cancelled by You
                                @elseif($status['status'] == 6)
                                    Order Cancelled by Admin
                                @endif
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-12">
                            <div class="ship_status_box"><span class="text-bold mr-2">Order Date:</span>{{$status->date}}</div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-lg-12 col-md-12 col-sm-12">
                            <ul class="track_order_list mt-4">
                                
                                @if ($status->status == 1)
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-write"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Placed</h4>
                                                <p>We have received your order</p>
                                            </div>
                                        </div>
                                    </li>
                                    <!-- Single List -->
                                    <li class="processing">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-package"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Waiting for Accept</h4>
                                                <p>Your order is pending.</p>
                                            </div>
                                        </div>
                                    </li>

                                    @if($status->order_type == 1)

                                    <!-- Single List -->
                                    <li>
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="fa fa-road"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order on the way</h4>
                                                <p>Your order is pending.</p>
                                            </div>
                                        </div>
                                    </li>

                                    <!-- Single List -->
                                    <li>
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-thumb-up"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order delivered</h4>
                                                <p>Your order is pending.</p>
                                            </div>
                                        </div>
                                    </li>

                                    @else
                                    
                                    <!-- Single List -->
                                    <li>
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-gift"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Picked up</h4>
                                                <p>Your order is pending.</p>
                                            </div>
                                        </div>
                                    </li>

                                    @endif
                                @endif

                                @if ($status->status == 2)
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-write"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Placed</h4>
                                                <p>We have received your order</p>
                                            </div>
                                        </div>
                                    </li>
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-package"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Accepted</h4>
                                                <p>Your order has been accepted.</p>
                                            </div>
                                        </div>
                                    </li>

                                    @if($status->order_type == 1)
                                    
                                    <!-- Single List -->
                                    <li class="processing">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="fa fa-road"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order on the way</h4>
                                                <p>Waiting for Shipping</p>
                                            </div>
                                        </div>
                                    </li>

                                    <!-- Single List -->
                                    <li>
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-thumb-up"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order delivered</h4>
                                                <p>Waiting for Shipping</p>
                                            </div>
                                        </div>
                                    </li>
                                    
                                    @else
                                    
                                    <!-- Single List -->
                                    <li class="processing">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-gift"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Picked up</h4>
                                                <p>Waiting for pickup.</p>
                                            </div>
                                        </div>
                                    </li>

                                    @endif
                                @endif

                                @if ($status->status == 3)
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-write"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Placed</h4>
                                                <p>We have received your order</p>
                                            </div>
                                        </div>
                                    </li>
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-package"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Accepted</h4>
                                                <p>Your order has been accepted.</p>
                                            </div>
                                        </div>
                                    </li>
                                    
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="fa fa-road"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order on the way</h4>
                                                <p>Order has been assigned to Delivery Boy.</p>
                                            </div>
                                        </div>
                                    </li>

                                    <!-- Single List -->
                                    <li class="processing">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-thumb-up"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order delivered</h4>
                                                <p>Order will be arrived shortly.</p>
                                            </div>
                                        </div>
                                    </li>
                                @endif

                                @if ($status->status == 4)
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-write"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Placed</h4>
                                                <p>We have received your order</p>
                                            </div>
                                        </div>
                                    </li>
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-package"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order ready</h4>
                                                <p>Your Order has been confirmed.</p>
                                            </div>
                                        </div>
                                    </li>
                                    
                                    @if($status->order_type == 1)
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="fa fa-road"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order on the way</h4>
                                                <p>Order has been assigned to Delivery Boy.</p>
                                            </div>
                                        </div>
                                    </li>

                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-thumb-up"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order delivered</h4>
                                                <p>Order has been delivered.</p>
                                            </div>
                                        </div>
                                    </li>
                                    
                                    @else
                                    
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="ti-gift"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order Picked up</h4>
                                                <p>Order has been Picked up.</p>
                                            </div>
                                        </div>
                                    </li>

                                    @endif
                                @endif

                                @if ($status->status == 5)                                    
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="fa fa-times"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order cancelled by you</h4>
                                                <p>Order has been cancelled by you.</p>
                                            </div>
                                        </div>
                                    </li>
                                @endif

                                @if ($status->status == 6)                                    
                                    <!-- Single List -->
                                    <li class="complete">
                                        <div class="trach_single_list">
                                            <div class="trach_icon_list"><i class="fa fa-times"></i></div>
                                            <div class="track_list_caption">
                                                <h4 class="mb-0">Order cancelled by admin</h4>
                                                <p>Order has been cancelled by admin.</p>
                                            </div>
                                        </div>
                                    </li>
                                @endif
                                
                            </ul>
                        </div>
                    </div>
                
                </div>
            </div>            
        </div>
    </div>
</section>
<!-- =========================== My All Orders =================================== -->
<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')