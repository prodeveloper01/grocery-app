@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
    <div class="container">
        <div class="row align-items-center">
            
            <div class="col-lg-12 col-md-12 col-sm-12 col-12">
                <div class="text-center">
                    <h2 class="breadcrumbs_title">My Orders</h2>
                    <nav aria-label="breadcrumb">
                      <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
                        <li class="breadcrumb-item active" aria-current="page">My Orders</li>
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
            
            <div class="col-lg-8 col-md-9">
            
                <!-- Order Items -->
                <div class="card style-2">
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th scope="col">Order No.</th>
                                        <th scope="col">Order Date</th>
                                        <th scope="col">Price</th>
                                        <th scope="col">Status</th>
                                        <th scope="col">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach ($orderdata as $row)
                                    <tr>
                                        <td><a href="{{URL::to('order-details/'.$row->id)}}"><b>{{$row->order_number}}</b></a></td>
                                        <td>{{$row->date}}</td>
                                        <td>${{number_format($row->total_price, 2)}}</td>
                                        <td>
                                            @if ($row['status'] == 1)
                                                Order Placed
                                            @elseif($row['status'] == 2)
                                                Order ready
                                            @elseif($row['status'] == 3)
                                                Order on the way
                                            @elseif($row['status'] == 4)
                                                Order delivered
                                            @elseif($row['status'] == 5)
                                                Order Cancelled <br> by You
                                            @elseif($row['status'] == 6)
                                                Order Cancelled <br> by Admin
                                            @endif
                                        </td>
                                        <td>
                                            <a href="{{URL::to('track-order/'.$row->id)}}" class="btn btn-sm btn-theme">Track Order</a>
                                            <a href="{{URL::to('order-details/'.$row->id)}}" class="btn btn-sm btn-theme">View Order</a>
                                        </td>
                                    </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>

                        <div class="row">
                            <div class="col-lg-12">
                                <nav aria-label="Page navigation example">
                                    @if ($orderdata->lastPage() > 1)
                                  <ul class="pagination">
                                    <li class="page-item left {{ ($orderdata->currentPage() == 1) ? ' disabled' : '' }}">
                                      <a class="page-link" href="{{ $orderdata->url(1) }}" aria-label="Previous">
                                        <span aria-hidden="true"><i class="ti-arrow-left mr-1"></i>Prev</span>
                                      </a>
                                    </li>
                                    @for ($i = 1; $i <= $orderdata->lastPage(); $i++)
                                    <li class="page-item {{ ($orderdata->currentPage() == $i) ? ' active' : '' }}"><a class="page-link" href="{{ $orderdata->url($i) }}">{{ $i }}</a></li>
                                    @endfor
                                    <li class="page-item right {{ ($orderdata->currentPage() == $orderdata->lastPage()) ? ' disabled' : '' }}">
                                      <a class="page-link" href="{{ $orderdata->url($orderdata->lastPage()) }}" aria-label="Previous">
                                        <span aria-hidden="true"><i class="ti-arrow-right mr-1"></i>Next</span>
                                      </a>
                                    </li>
                                  </ul>
                                  @endif
                                </nav>
                            </div>
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