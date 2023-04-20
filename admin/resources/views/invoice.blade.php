@extends('theme.default')
<style type="text/css">
    @media  print {
      @page  { margin: 0; }
      body { margin: 1.6cm; }
    }
</style>
@section('content')
<!-- row -->

<div class="row page-titles mx-0">
    <div class="col p-md-0">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="{{URL::to('/admin/home')}}">Dashboard</a></li>
            <li class="breadcrumb-item active"><a href="javascript:void(0)">Invoice</a></li>
        </ol>
    </div>
</div>
<!-- row -->

<div class="container-fluid">
    <!-- End Row -->
    <div class="card" id="printDiv">
        <div class="card-header">
            Invoice : 
            <strong>{{$getusers->order_number}}</strong> 
            <span class="float-right"> <strong>Order Status :</strong> 
                @if($getusers->status == '1')
                    Order Received
                @elseif ($getusers->status == '2')
                    On the way
                @elseif ($getusers->status == '3')
                    Assigned to Driver
                @elseif ($getusers->status == '4')
                    Delivered
                @elseif ($getusers->status == '5')
                    Cancelled by User
                @elseif ($getusers->status == '6')
                    Cancelled by Admin
                @endif
            </span>

        </div>
        <div class="card-body">
            <div class="row mb-4">
                <div class="col-sm-6">
                    <h6 class="mb-3">To:</h6>
                    <div>
                        <strong>{{$getusers['users']->name}}</strong>
                    </div>
                    <div><strong>Email:</strong> {{$getusers['users']->email}}</div>
                    <div><strong>Mobile:</strong> {{$getusers['users']->mobile}}</div>
                    <div><strong>Address:</strong> {{$getusers->address}} </div>
                    <div><strong>Building:</strong> {{$getusers->building}}</div>
                    <div><strong>Landmark:</strong> {{$getusers->landmark}}</div>
                    <div><strong>Pincode:</strong> {{$getusers->pincode}}</div>
                </div>


            </div>

            <div class="table-responsive-sm">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th class="center">#</th>
                            <th>Item</th>
                            <th class="right">Variation</th>
                            <th class="right">Unit Cost</th>
                            <th class="center">Qty</th>
                            <th class="right">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php
                        $i=1;
                        foreach ($getorders as $orders) {
                        ?>
                        <tr>
                            <td class="center">{{$i}}</td>
                            <td class="left strong">{{$orders->item_name}}</td>
                            <td class="left strong">{{$orders->weight}}</td>
                            <td class="left">{{Auth::user()->currency}}{{number_format($orders->price,2)}}</td>
                            <td class="center">{{$orders->qty}}</td>
                            <td class="right">{{Auth::user()->currency}}{{number_format($orders->qty * $orders->price,2)}}</td>
                        </tr>
                        <?php
                        $i++;
                        }
                        ?>
                    </tbody>
                </table>
            </div>
            <div class="row">
                <div class="col-lg-4 col-sm-5">

                </div>

                <div class="col-lg-4 col-sm-5 ml-auto">
                    <table class="table table-clear">
                        <tbody>
                            <tr>
                                <td class="left">
                                    <strong>Tax</strong> ({{number_format($getusers->tax,2)}}%)
                                </td>
                                <td class="right">
                                    <strong>{{Auth::user()->currency}}{{number_format($getusers->tax_amount,2)}}</strong>
                                </td>
                            </tr>
                            <tr>
                                <td class="left">
                                    <strong>Delivery Charge</strong>
                                </td>
                                <td class="right">
                                    <strong>{{Auth::user()->currency}}{{number_format($getusers->delivery_charge,2)}}</strong>
                                </td>
                            </tr>
                            <tr>
                                <td class="left">
                                    <strong>Discount</strong> ({{$getusers->promocode}})
                                </td>
                                <td class="right">
                                    <strong>{{Auth::user()->currency}}{{number_format($getusers->discount_amount,2)}}</strong>
                                </td>
                            </tr>
                            <tr>
                                <td class="left">
                                    <strong>Total</strong>
                                </td>
                                <td class="right">
                                    <strong>{{Auth::user()->currency}}{{number_format($getusers->order_total,2)}}</strong>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                </div>

            </div>

        </div>
    </div>
    <!-- End Row -->
    <button type="button" class="btn btn-primary float-right" id="doPrint">
        <i class="fa fa-print" aria-hidden="true"></i> Print
    </button>
</div>
<!-- #/ container -->

<!-- #/ container -->
@endsection
@section('script')
<script type="text/javascript">
    document.getElementById("doPrint").addEventListener("click", function() {
         var printContents = document.getElementById('printDiv').innerHTML;
         var originalContents = document.body.innerHTML;
         document.body.innerHTML = printContents;
         window.print();
         document.body.innerHTML = originalContents;
    });
</script>
@endsection