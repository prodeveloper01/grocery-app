@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">My Wallet</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">Wallet</li>
					  </ol>
					</nav>
				</div>
			</div>
			
		</div>
	</div>
</div>
<!-- =========================== Breadcrumbs =================================== -->
<?php 
$strings = array('#D0EEF9','#FEEBD1','#F0D7F9','#FEE3DC','#EAF8ED','#ecd9e9','#ffcfd7');
$count = count($strings);
?>
<!-- =========================== Account Settings =================================== -->
<section class="gray">
	<div class="container">
		<div class="row">
		
			<div class="col-lg-4">
                <div class="order-payment-summary" style="background-color: #1d2738">
                    <div class="col-4 mx-auto text-center">
                        <img src='{!! asset("public/assets/images/ic_wallet.png") !!}' width="100px" alt="" class="text-center">
                    </div>
                    
                    <h2 class="text-center mt-3" style="color: #fff;">Wallet Balance</h2>
                    <h1 class="text-center" style="color: #fff;"><span>{{$getdata->currency}}{{number_format($walletamount->wallet, 2)}}</span></h1>
                </div>
                @foreach($getpaymentdata as $paymentdata)
                
                    @if ($paymentdata->payment_name == "RazorPay")
                        <div class="mt-3">
                            <button type="button" data-toggle="modal" data-target="#AddMoneypay" style="width: 100%;" class="btn btn-go-cart btn-theme">Add Money from RazorPay</button>
                        </div>

                        @if($paymentdata->environment=='1')
                            <input type="hidden" name="razorpay" id="razorpay" value="{{$paymentdata->test_public_key}}">
                        @else
                            <input type="hidden" name="razorpay" id="razorpay" value="{{$paymentdata->live_public_key}}">
                        @endif

                    @endif

                    @if ($paymentdata->payment_name == "Stripe")
                        <div class="mt-3">
                            <button type="button" data-toggle="modal" data-target="#AddMoneyStripe" style="width: 100%;" class="btn btn-go-cart btn-theme">Add Money from Stripe</button>
                        </div>

                        @if($paymentdata->environment=='1')
                            <input type="hidden" name="stripe" id="stripe" value="{{$paymentdata->test_public_key}}">
                        @else
                            <input type="hidden" name="stripe" id="stripe" value="{{$paymentdata->live_public_key}}">
                        @endif
                    @endif

                @endforeach
            </div>
			
			<div class="col-lg-8 col-md-9">
				@foreach ($transaction_data as $orders)
				    @if ($orders->transaction_type == 1)
				    <div class="order-details-box">
				        <div class="wallet-details-img">
				            <img src='{!! asset("public/assets/images/ic_trGreen.png") !!}' alt="" class="mt-1">
				        </div>
				        <div class="order-details-name mt-3">
				            <h3> {{$orders->order_number}} <span style="color: #000; float: right;">{{$orders->date}}</span></h3>
				            <h3><span style="color: #ff0000;">Order Cancelled</span> <span style="color: #00c56a; float: right;"> {{$getdata->currency}}{{number_format($orders->wallet, 2)}}</span></h3>
				        </div>
				    </div>
				    @elseif ($orders->transaction_type == 2)

				    <div class="order-details-box">
				        <div class="wallet-details-img">
				            <img src='{!! asset("public/assets/images/ic_trRed.png") !!}' alt="" class="mt-1">
				        </div>
				        <div class="order-details-name mt-3">
				            <h3> {{$orders->order_number}} <span style="color: #000; float: right;">{{$orders->date}}</span></h3>
				            <h3><span style="color: #00c56a;">Order Confirmed</span> <span style="color: #ff0000; float: right;"> - {{$getdata->currency}}{{number_format($orders->wallet, 2)}}</span></h3>
				        </div>
				    </div>

				    @elseif ($orders->transaction_type == 3)

				        <div class="order-details-box">
				            <div class="wallet-details-img">
				                <img src='{!! asset("public/assets/images/ic_trGreen.png") !!}' alt="" class="mt-1">
				            </div>
				            <div class="order-details-name mt-3">
				                <a href="javascript:void(0)">
				                    <a href="#">
				                        <h3> {{$orders->username}} <span style="color: #000; float: right;">{{$orders->date}}</span></h3>
				                    </a>
				                </a>
				                <h3><span style="color: #00c56a;">Referral Earning</span> <span style="color: #00c56a; float: right;">{{$getdata->currency}}{{number_format($orders->wallet, 2)}}</span></h3>
				            </div>
				        </div>

                    @elseif ($orders->transaction_type == 4)

                    <div class="order-details-box">
                        <div class="wallet-details-img">
                            <img src='{!! asset("public/assets/images/ic_trGreen.png") !!}' alt="" class="mt-1">
                        </div>
                        <div class="order-details-name mt-3">
                            <h3> Wallet Recharge <span style="color: #000; float: right;">{{$orders->date}}</span></h3>
                            <h3><span style="color: #00c56a;">
                                @if ($orders->order_type == 3)
                                    RazorPay
                                @else
                                    Stripe
                                @endif
                            </span> <span style="color: #00c56a; float: right;">{{$getdata->currency}}{{number_format($orders->wallet, 2)}}</span></h3>
                        </div>
                    </div>

				    @endif
				@endforeach

				<div class="row">
					<div class="col-lg-12">
						<nav aria-label="Page navigation example">
							@if ($transaction_data->lastPage() > 1)
						  <ul class="pagination">
							<li class="page-item left {{ ($transaction_data->currentPage() == 1) ? ' disabled' : '' }}">
							  <a class="page-link" href="{{ $transaction_data->url(1) }}" aria-label="Previous">
								<span aria-hidden="true"><i class="ti-arrow-left mr-1"></i>Prev</span>
							  </a>
							</li>
							@for ($i = 1; $i <= $transaction_data->lastPage(); $i++)
							<li class="page-item {{ ($transaction_data->currentPage() == $i) ? ' active' : '' }}"><a class="page-link" href="{{ $transaction_data->url($i) }}">{{ $i }}</a></li>
							@endfor
							<li class="page-item right {{ ($transaction_data->currentPage() == $transaction_data->lastPage()) ? ' disabled' : '' }}">
							  <a class="page-link" href="{{ $transaction_data->url($transaction_data->lastPage()) }}" aria-label="Previous">
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

	<!-- Modal Add Money RazorPay-->
	<div class="modal fade text-left" id="AddMoneypay" tabindex="-1" role="dialog" aria-labelledby="RditProduct"
	aria-hidden="true">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <label class="modal-title text-text-bold-600" id="RditProduct">Add Money to Wallet</label>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div id="errorr" style="color: red;"></div>
	      
	      <form method="post" id="change_password_form">
	      {{csrf_field()}}
	        <div class="modal-body">

	          <label>Amount </label>
	          <div class="form-group">
	            <input type="text" name="add_money" id="add_money" class="form-control" required="">
	            <input type="hidden" name="user_id" id="user_id" class="form-control" value="{{Session::get('id')}}">
	          </div>

	        </div>
	        <div class="modal-footer">
	          <input type="reset" class="btn col-md-6 btn-dark" data-dismiss="modal"
	          value="Close">
	          <input type="button" class="btn col-md-6 btn-dark addmoney" value="Submit">
	        </div>
	      </form>
	    </div>
	  </div>
	</div>

    <!-- Modal Add Money Stripe-->
    <div class="modal fade text-left" id="AddMoneyStripe" tabindex="-1" role="dialog" aria-labelledby="RditProduct"
    aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <label class="modal-title text-text-bold-600" id="RditProduct">Add Money to Wallet</label>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div id="errorr" style="color: red;"></div>
          
          <form method="post" id="change_password_form">
          {{csrf_field()}}
            <div class="modal-body">

              <label>Amount </label>
              <div class="form-group">
                <input type="text" name="add_money_stripe" id="add_money_stripe" class="form-control" required="">
                <input type="hidden" name="user_id" id="user_id" class="form-control" value="{{Session::get('id')}}">
                <input type="hidden" name="email" id="email" class="form-control" value="{{Session::get('email')}}">
              </div>

            </div>
            <div class="modal-footer">
              <input type="reset" class="btn col-md-6 btn-dark" data-dismiss="modal"
              value="Close">
              <input type="button" class="btn col-md-6 btn-dark addmoneystripe" value="Submit">
            </div>
          </form>
        </div>
      </div>
    </div>
</section>
<!-- =========================== Account Settings =================================== -->
<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')

<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<script src="https://checkout.stripe.com/v2/checkout.js"></script>

<script type="text/javascript">
    var SITEURL = '{{URL::to('')}}';
    var handler = StripeCheckout.configure({
      key: $('#stripe').val(),
      image: 'https://stripe.com/img/documentation/checkout/marketplace.png',
      locale: 'auto',
      token: function(token) {
        // You can access the token ID with `token.id`.
        // Get the token ID to your server-side code for use.

        var add_money = parseFloat($('#add_money_stripe').val());
        var token = token.id;


        $('#preloader').show();

        $.ajax({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            },
            url: SITEURL + '/addmoneystripe',
            data: {
                add_money : add_money ,
                stripeToken : token,
            }, 
            method: 'POST',
            success: function(response) {
                $('#preloader').hide();
                if (response.status == 1) {
                    window.location.href = SITEURL + '/wallet';
                } else {
                    $('#ermsg').text(response.message);
                    $('#error-msg').addClass('alert-danger');
                    $('#error-msg').css("display","block");

                    setTimeout(function() {
                        $("#error-msg").hide();
                    }, 5000);
                }
            },
            error: function(error) {

                // $('#errormsg').show();
            }
        });
      },
      opened: function() {
        // console.log("Form opened");
      },
      closed: function() {
        // console.log("Form closed");
      }
    });

    $(document).ready(function() {
        $.ajaxSetup({
          headers: {
              'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
          }
        }); 
        $('body').on('click', '.addmoney', function(e){
            var add_money = parseFloat($('#add_money').val());

            var options = {
                "key": $('#razorpay').val(),
                "amount": (parseInt(add_money*100)), // 2000 paise = INR 20
                "name": "Grocery",
                "description": "Add Money to wallet",
                "image": "{!! asset('public/front/img/logo-light.png') !!}",
                "handler": function (response){
                    $('#preloader').show();
                    $.ajax({
                        url: SITEURL + '/addmoney',
                        type: 'post',
                        dataType: 'json',
                        data: {
                            razorpay_payment_id: response.razorpay_payment_id ,
                            add_money : add_money
                        }, 
                        success: function (msg) {
                        $('#preloader').hide();
                        window.location.href = SITEURL + '/wallet';
                    }
                });
            
            },
                "prefill": {
                    "contact": '{{Auth::user()->mobile}}',
                    "email":   '{{Auth::user()->email}}',
                    "name":   '{{Auth::user()->name}}',
                },
                "theme": {
                    "color": "#fe734c"
                }
            };

            var rzp1 = new Razorpay(options);
            rzp1.open();
            e.preventDefault();
        });

        $('body').on('click', '.addmoneystripe', function(e){
            
            var add_money = parseFloat($('#add_money_stripe').val());
            var email = '{{Auth::user()->email}}';

            handler.open({
                name: 'Food App',
                description: 'Add Money to wallet',
                amount: add_money*100,
                email: email
            });
            e.preventDefault();
            // Close Checkout on page navigation:
            $(window).on('popstate', function() {
              handler.close();
            });
            
        });
    });


    $('#add_money').keyup(function(){
        var val = $(this).val();
        if(isNaN(val)){
             val = val.replace(/[^0-9\.]/g,'');
             if(val.split('.').length>2) 
                 val =val.replace(/\.+$/,"");
        }
        $(this).val(val); 
    });
    $('#add_money_stripe').keyup(function(){
        var val = $(this).val();
        if(isNaN(val)){
             val = val.replace(/[^0-9\.]/g,'');
             if(val.split('.').length>2) 
                 val =val.replace(/\.+$/,"");
        }
        $(this).val(val); 
    });
</script>