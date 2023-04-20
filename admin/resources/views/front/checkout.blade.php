@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">CHECKOUT</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">CHECKOUT</li>
					  </ol>
					</nav>
				</div>
			</div>
			
		</div>
	</div>
</div>
<!-- =========================== Breadcrumbs =================================== -->

<!-- =========================== Cart =================================== -->
<section>
	<div class="container">
		<div class="row">
			
			<div class="col-lg-7 col-md-12 col-sm-12 col-12">
				<div class="table-responsive">
					<table class="table">
						<thead>
							<tr>
								<th scope="col">Product</th>
								<th scope="col">Price</th>
								<th scope="col">Quantity</th>
								<th scope="col">Total</th>
							</tr>
						</thead>
						<tbody>
							@foreach ($cart as $cartitems)
							<tr>
								<td>
									<div class="tbl_cart_product">
										<div class="tbl_cart_product_thumb">
											<img src="{{$cartitems['itemimage']->image}}" class="img-fluid-cart" alt="" />
										</div>
										<div class="tbl_cart_product_caption">
											<h5 class="tbl_pr_title">{{$cartitems->item_name}}</h5>
											<span class="tbl_pr_quality theme-cl">{{$cartitems->weight}}</span>
										</div>
									</div>
								</td>
								<td><h4 class="tbl_org_price">{{$getdata->currency}}{{number_format($cartitems->price, 2)}}</h4></td>
								<td class="text-center">
									<span>{{$cartitems->qty}}</span>
								</td>
								<td>
									<div class="tbl_pr_action">
										<h5 class="tbl_total_price">{{$getdata->currency}}{{number_format($cartitems->qty * $cartitems->price, 2)}}</h5>
									</div>
								</td>
							</tr>

							<?php
							$data[] = array(
							    "total_price" => $cartitems->qty * $cartitems->price
							);
							$subtotal = array_sum(array_column(@$data, 'total_price'));
							$taxprice = array_sum(array_column(@$data, 'total_price'))*$getdata->tax/100; 
							?>
							@endforeach
						</tbody>
					</table>
				</div>
				
			</div>
			
			<div class="col-lg-5 col-md-12 col-sm-12 col-12">
				<div class="cart_detail_box mb-4">
					<div class="card-body">
						<ul class="list-group list-group-sm list-group-flush-y list-group-flush-x">
							<li class="list-group-item d-flex">
								<h5 class="mb-0">Order Summary</h5>
							</li>
							<li class="list-group-item d-flex">
								<span>Subtotal</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($subtotal, 2)}}</span>
							</li>
							<li class="list-group-item d-flex">
								<span>Tax ({{$getdata->tax}}%)</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($taxprice, 2)}}</span>
							</li>
							@if (Session::get('order_type') == "1")
							<li class="list-group-item d-flex delivery_charge_section">
								<span>Delivery Charge</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($getdata->delivery_charge, 2)}}</span>
							</li>
							@endif

							@if (Session::has('offer_amount'))
								<li class="list-group-item d-flex">
									<span>Discount ({{Session::get('offer_code')}})</span> <span class="ml-auto font-size-sm">-{{$getdata->currency}}{{number_format($subtotal*Session::get('offer_amount')/100, 2)}}</span>
								</li>
							@endif

							@if (Session::get('order_type') == "1")
								@if (Session::has('offer_amount'))
									<li class="list-group-item d-flex font-size-lg font-weight-bold">
										<span>Total</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($subtotal + $getdata->delivery_charge + $taxprice-$subtotal*Session::get('offer_amount')/100, 2)}}</span>
									</li>
									<input type="hidden" name="total_amount" id="total_amount" value="{{number_format($subtotal + $getdata->delivery_charge + $taxprice-$subtotal*Session::get('offer_amount')/100, 2)}}">

									<input type="hidden" name="discount_amount" id="discount_amount" value="{{$subtotal*Session::get('offer_amount')/100}}">
								@else
								    <li class="list-group-item d-flex font-size-lg font-weight-bold">
								    	<span>Total</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($subtotal + $taxprice + $getdata->delivery_charge, 2)}}</span>
								    </li>
								    <input type="hidden" name="total_amount" id="total_amount" value="{{number_format($subtotal + $taxprice + $getdata->delivery_charge, 2)}}">
								    <input type="hidden" name="discount_amount" id="discount_amount" value="">
								@endif
							@endif

							@if (Session::get('order_type') == "2")
								@if (Session::has('offer_amount'))
									<li class="list-group-item d-flex font-size-lg font-weight-bold">
										<span>Total</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($subtotal + $taxprice-$subtotal*Session::get('offer_amount')/100, 2)}}</span>
									</li>
									<input type="hidden" name="total_amount" id="total_amount" value="{{number_format($subtotal + $taxprice-$subtotal*Session::get('offer_amount')/100, 2)}}">

									<input type="hidden" name="discount_amount" id="discount_amount" value="{{$subtotal*Session::get('offer_amount')/100}}">
								@else
								    <li class="list-group-item d-flex font-size-lg font-weight-bold">
								    	<span>Total</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($subtotal + $taxprice, 2)}}</span>
								    </li>
								    <input type="hidden" name="total_amount" id="total_amount" value="{{number_format($subtotal + $taxprice, 2)}}">
								    <input type="hidden" name="discount_amount" id="discount_amount" value="">
								@endif
							@endif

						</ul>
					</div>

					<input type="hidden" name="subtotal" id="subtotal" value="{{$subtotal}}" readonly="">
					<input type="hidden" name="email" id="email" value="{{Auth::user()->email}}" readonly="">
					<input type="hidden" name="delivery_charge" id="delivery_charge" value="{{$getdata->delivery_charge}}" readonly="">
					<input type="hidden" name="tax" id="tax" value="{{$getdata->tax}}" readonly="">
					<input type="hidden" name="tax_amount" id="tax_amount" value="{{number_format($taxprice, 2)}}" readonly="">
					<input type="hidden" name="promocode" id="promocode" value="{{Session::get('offer_code')}}" readonly="">
					<input type="hidden" name="discount_pr" id="discount_pr" value="{{Session::get('offer_amount')}}" readonly="">
					<input type="hidden" name="order_type" id="order_type" value="{{Session::get('order_type')}}" readonly="">

					@if (Session::get('ordered_date') != "")
					<div class="col-lg-12 col-md-12">
						<div class="form-group">
							<label>Order Date</label>
							<input type="text" class="form-control" name="ordered_date" id="ordered_date" readonly="" value="{{Session::get('ordered_date')}}">
						</div>
					</div>
					@endif

					@if (Session::get('order_type') == "1")
					<div id="address-data">
						<div class="col-lg-12 col-md-12">
							<div class="form-group">
								<label>Address</label>
								<input type="text" class="form-control" name="address" id="address" placeholder="Address" value="{{Session::get('address')}}" readonly="">

								<input type="hidden" id="lat" name="lat" value="{{Session::get('lat')}}" readonly=""/>
								<input type="hidden" id="lang" name="lang" value="{{Session::get('lang')}}" readonly=""/>
								<input type="hidden" id="city" name="city" placeholder="city" value="{{Session::get('city')}}" readonly=""/> 
								<input type="hidden" id="state" name="state" placeholder="state" value="{{Session::get('state')}}" readonly=""/> 
								<input type="hidden" id="country" name="country" placeholder="country" value="{{Session::get('country')}}" readonly=""/>
							</div>
						</div>
						<div class="col-lg-12 col-md-12">
							<div class="form-group">
								<label>Pincode</label>
								<input type="text" class="form-control" name="pincode" id="pincode" placeholder="Pincode" value="{{Session::get('pincode')}}" readonly="">
							</div>
						</div>
						<div class="col-lg-12 col-md-12">
							<div class="form-group">
								<label>Door / Flat no.</label>
								<input type="text" class="form-control" name="building" id="building" placeholder="Door / Flat no." value="{{Session::get('building')}}" readonly="">
							</div>
						</div>
						<div class="col-lg-12 col-md-12">
							<div class="form-group">
								<label>Landmark</label>
								<input type="text" class="form-control" name="landmark" id="landmark" placeholder="Landmark" value="{{Session::get('landmark')}}" readonly="">
							</div>
						</div>						
					</div>
					@endif
					@if (Session::get('notes') != "")
					<div class="col-lg-12 col-md-12">
						<div class="form-group">
							<label>Notes</label>
							<textarea class="form-control" name="notes" id="notes" placeholder="Order Notes">{{Session::get('notes')}}</textarea>
						</div>
					</div>
					@endif
				</div>

				<a class="btn btn-block btn-dark mb-2" href="#" onclick="WalletOrder()">My Wallet ({{$getdata->currency}}{{number_format($userinfo->wallet, 2)}})</a>

				@foreach($getpaymentdata as $paymentdata)

				    @if ($paymentdata->payment_name == "COD")
				    	<a class="btn btn-block btn-dark mb-2" href="#" onclick="CashonDelivery()">Cash Payment</a>
				    @endif

				    @if ($paymentdata->payment_name == "RazorPay")
				    	<a class="btn btn-block buy_now btn-dark mb-2" href="#">RazorPay Payment</a>

				        @if($paymentdata->environment=='1')
				            <input type="hidden" name="razorpay" id="razorpay" value="{{$paymentdata->test_public_key}}" readonly="">
				        @else
				            <input type="hidden" name="razorpay" id="razorpay" value="{{$paymentdata->live_public_key}}" readonly="">
				        @endif

				    @endif

				    @if ($paymentdata->payment_name == "Stripe")
				    	<a class="btn btn-block btn-dark mb-2" id="customButton" href="#" style="display: none;">Stripe Payment</a>
				    	<a class="btn btn-block stripe btn-dark mb-2" href="#" onclick="stripe()">Stripe Payment</a>

				        @if($paymentdata->environment=='1')
				            <input type="hidden" name="stripe" id="stripe" value="{{$paymentdata->test_public_key}}" readonly="">
				        @else
				            <input type="hidden" name="stripe" id="stripe" value="{{$paymentdata->live_public_key}}" readonly="">
				        @endif
				    @endif

				@endforeach

				<a class="px-0 text-body" href="{{URL::to('/')}}"><i class="ti-back-left mr-2"></i> Continue Shopping</a>
			</div>
			
			
		</div>
	</div>
</section>
<!-- =========================== Cart =================================== -->


<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<script src="https://checkout.stripe.com/v2/checkout.js"></script>
<script type="text/javascript">

//Cashondelivery Payment
function CashonDelivery() 
{
    var subtotal = parseFloat($('#subtotal').val());
    var tax = parseFloat($('#tax').val());
    var delivery_charge = parseFloat($('#delivery_charge').val());
    var discount_amount = parseFloat($('#discount_amount').val());
    var total_amount = parseFloat($('#total_amount').val());
    var notes = $('#notes').val();
    var address = $('#address').val();
    var promocode = $('#promocode').val();
    var tax_amount = $('#tax_amount').val();
    var discount_pr = $('#discount_pr').val();
    var lat = $('#lat').val();
    var lang = $('#lang').val();
    var pincode = $('#pincode').val();
    var building = $('#building').val();
    var landmark = $('#landmark').val();
    var order_type = $('#order_type').val();
    var ordered_date = $('#ordered_date').val();

    $('#preloader').show();
    $.ajax({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        },
        url:"{{ URL::to('/orders/cashondelivery') }}",
        data: {
            subtotal : subtotal ,
            tax : tax,
            delivery_charge : delivery_charge ,
            discount_amount: discount_amount , 
            total_amount : total_amount ,
            notes : notes,
            address: address , 
            promocode: promocode ,
            tax_amount : tax_amount,
			discount_pr: discount_pr , 
			lat : lat,
            lang : lang,
            pincode : pincode,
            building : building,
            landmark : landmark,
            order_type : order_type,
            ordered_date : ordered_date,
        }, 
        method: 'POST',
        success: function(response) {
            $('#preloader').hide();
            if (response.status == 1) {
                window.location.href = "{{ URL::to('/orders') }}";
            } else {
                toast.error(response.message);
            }
        },
        error: function(error) {
        	toast.error("Something went wrong");
        }
    });
    
}

//Wallet Payment
function WalletOrder() 
{
    var subtotal = parseFloat($('#subtotal').val());
    var tax = parseFloat($('#tax').val());
    var delivery_charge = parseFloat($('#delivery_charge').val());
    var discount_amount = parseFloat($('#discount_amount').val());
    var total_amount = parseFloat($('#total_amount').val());
    var notes = $('#notes').val();
    var address = $('#address').val();
    var promocode = $('#promocode').val();
    var tax_amount = $('#tax_amount').val();
    var discount_pr = $('#discount_pr').val();
    var lat = $('#lat').val();
    var lang = $('#lang').val();
    var pincode = $('#pincode').val();
    var building = $('#building').val();
    var landmark = $('#landmark').val();
    var order_type = $('#order_type').val();
    var ordered_date = $('#ordered_date').val();

    $('#preloader').show();
    $.ajax({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        },
        url:"{{ URL::to('/orders/walletorder') }}",
        data: {
            subtotal : subtotal ,
            tax : tax,
            delivery_charge : delivery_charge ,
            discount_amount: discount_amount , 
            total_amount : total_amount ,
            notes : notes,
            address: address , 
            promocode: promocode ,
            tax_amount : tax_amount,
			discount_pr: discount_pr , 
			lat : lat,
            lang : lang,
            pincode : pincode,
            building : building,
            landmark : landmark,
            order_type : order_type,
            ordered_date : ordered_date,
        }, 
        method: 'POST',
        success: function(response) {
            $('#preloader').hide();
            if (response.status == 1) {
                window.location.href = "{{ URL::to('/orders') }}";
            } else {
                toast.error(response.message);
            }
        },
        error: function(error) {

            // $('#errormsg').show();
        }
    });
}

//Razorpay Payment
$('body').on('click', '.buy_now', function(e){
	var subtotal = parseFloat($('#subtotal').val());
	var order_total = parseFloat($('#total_amount').val());
	var tax = parseFloat($('#tax').val());
	var delivery_charge = parseFloat($('#delivery_charge').val());
	var discount_amount = parseFloat($('#discount_amount').val());
	var notes = $('#notes').val();
	var address = $('#address').val();
	var promocode = $('#promocode').val();
	var tax_amount = $('#tax_amount').val();
	var discount_pr = $('#discount_pr').val();
	var lat = $('#lat').val();
	var lang = $('#lang').val();
	var building = $('#building').val();
	var landmark = $('#landmark').val();
	var pincode = $('#pincode').val();
	var city = $('#city').val();
	var state = $('#state').val();
	var country = $('#country').val();
	var order_type = $('#order_type').val();
	var ordered_date = $('#ordered_date').val();

	if (order_type == "1") {
	    if (address == "" && lat == "" && lang == "") {         
	        toast.error('Address is required');
	    } else if (lat == "") {
	        toast.error('Please select the address from suggestion');
	    } else if (lang == "") {
	        toast.error('Please select the address from suggestion');
	    } else if (building == "") {
	        toast.error('Door / Flat no. is required');
	    } else if (landmark == "") {
	    	toast.error('Landmark is required');
	    } else {
	    	$('#preloader').show();
	        $.ajax({
	            headers: {
	                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
	            },
	            url:"{{ URL::to('/orders/checkpincode') }}",
		        data: {
		            pincode: pincode,
		            subtotal: subtotal,
		        },
	            method: 'POST',
	            success: function(result) {
	            	$('#preloader').hide();
	                if (result.status == 1) {
	                    var options = {
	                        "key": $('#razorpay').val(),
	                        "amount": (parseInt(order_total*100)), // 2000 paise = INR 20
	                        "name": "Grocery",
	                        "description": "Order Value",
	                        "image": "{!! asset('public/front/img/logo-light.png') !!}",
	                        "handler": function (response){
	                            $('#preloader').show();
	                            $.ajax({
	                            	headers: {
	                            	    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
	                            	},
	                                url: "{{ URL::to('orders/payment') }}",
	                                type: 'post',
	                                dataType: 'json',
	                                data: {
	                                    order_total : order_total ,
	                                    razorpay_payment_id: response.razorpay_payment_id ,
	                                    address: address , 
	                                    promocode: promocode , 
	                                    discount_amount: discount_amount , 
	                                    discount_pr: discount_pr , 
	                                    tax : tax,
	                                    tax_amount : tax_amount,
	                                    delivery_charge : delivery_charge ,
	                                    notes : notes,
	                                    order_type : order_type,
	                                    lat : lat,
	                                    lang : lang,
	                                    building : building,
	                                    landmark : landmark,
	                                    pincode : pincode,
	                                    city : city,
	                                    state : state,
	                                    country : country,
	                                    ordered_date : ordered_date,
	                                }, 
	                                success: function (msg) {
	                                $('#preloader').hide();
	                                window.location.href = "{{ URL::to('/orders') }}";
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
	                } else {
	                    toast.error(result.message);
	                }
	            },
	        });
	    }
	} else {
		$('#preloader').show();
        $.ajax({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            },
            url:"{{ URL::to('/orders/checkpincode') }}",
            data: {
                subtotal: subtotal,
            },
            method: 'POST',
            success: function(result) {
            	$('#preloader').hide();
                if (result.status == 1) {
                    var options = {
                        "key": $('#razorpay').val(),
                        "amount": (parseInt(order_total*100)), // 2000 paise = INR 20
                        "name": "Grocery",
                        "description": "Order Value",
                        "image": "{!! asset('public/front/img/logo-light.png') !!}",
                        "handler": function (response){
                            $('#preloader').show();
                            $.ajax({
                            	headers: {
                            	    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                            	},
                                url: "{{ URL::to('orders/payment') }}",
                                type: 'post',
                                dataType: 'json',
                                data: {
                                    order_total : order_total ,
                                    razorpay_payment_id: response.razorpay_payment_id ,
                                    address: address , 
                                    promocode: promocode , 
                                    discount_amount: discount_amount , 
                                    discount_pr: discount_pr , 
                                    tax : tax,
                                    tax_amount : tax_amount,
                                    delivery_charge : delivery_charge ,
                                    notes : notes,
                                    order_type : order_type,
                                    lat : lat,
                                    lang : lang,
                                    building : building,
                                    landmark : landmark,
                                    pincode : pincode,
                                    city : city,
                                    state : state,
                                    country : country,
                                    ordered_date : ordered_date,
                                }, 
                                success: function (msg) {
                                $('#preloader').hide();
                                window.location.href = "{{ URL::to('/orders') }}";
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
                } else {
                    toast.error(result.message);
                }
            },
        });
    }
});

//Stripe Payment
function stripe() {
    var pincode = $('#pincode').val();
    var subtotal = $('#subtotal').val();
    $('#preloader').show();
    $.ajax({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        },
        url:"{{ URL::to('/orders/checkpincode') }}",
        data: {
            pincode: pincode,
            subtotal: subtotal,
        },
        method: 'POST',
        success: function(result) {
        	$('#preloader').hide();
            if (result.status == 1) {
                $("#customButton").click();
            } else {
                toast.error("Something went wrong");
            }
        },
    });
}

$('#customButton').on('click', function(e) {
    // Open Checkout with further options:
    var total_amount = parseFloat($('#total_amount').val());
    var subtotal = parseFloat($('#subtotal').val());
    var order_type = $('#order_type').val();
    var email = $('#email').val();
    var pincode = $('#pincode').val();

    if (order_type == "1") {
        handler.open({
            name: 'Grocery App',
            description: 'Grocery Service',
            amount: total_amount*100,
            email: email
        });
        e.preventDefault();
        // Close Checkout on page navigation:
        $(window).on('popstate', function() {
          handler.close();
        });
    } else {
    	$('#preloader').show();
        $.ajax({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            },
            url:"{{ URL::to('/orders/checkpincode') }}",
            data: {
                pincode: pincode,
                subtotal: subtotal,
            },
            method: 'POST',
            success: function(result) {
            	$('#preloader').hide();
                if (result.status == 1) {
                    handler.open({
                        name: 'Grocery App',
                        description: 'Grocery Service',
                        amount: total_amount*100,
                        email: email
                    });
                    e.preventDefault();
                    // Close Checkout on page navigation:
                    $(window).on('popstate', function() {
                      handler.close();
                    });
                } else {
                    toast.error("Somethingm went wrong");
                }
            },
        });
    }
});

var handler = StripeCheckout.configure({
  key: $('#stripe').val(),
  image: 'https://stripe.com/img/documentation/checkout/marketplace.png',
  locale: 'auto',
  token: function(token) {
    // You can access the token ID with `token.id`.
    // Get the token ID to your server-side code for use.

    var order_total = parseFloat($('#total_amount').val());
    var tax = parseFloat($('#tax').val());
    var delivery_charge = parseFloat($('#delivery_charge').val());
    var discount_amount = parseFloat($('#discount_amount').val());
    var notes = $('#notes').val();
    var address = $('#address').val();
    var promocode = $('#promocode').val();
    var tax_amount = $('#tax_amount').val();
    var discount_pr = $('#discount_pr').val();
    var lat = $('#lat').val();
    var lang = $('#lang').val();
    var building = $('#building').val();
    var landmark = $('#landmark').val();
    var pincode = $('#pincode').val();
    var city = $('#city').val();
    var state = $('#state').val();
    var country = $('#country').val();
    var order_type = $('#order_type').val();
    var ordered_date = $('#ordered_date').val();
    var token = token.id;


    $('#preloader').show();

    $.ajax({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        },
        url:"{{ URL::to('orders/charge') }}",
        data: {
            order_total : order_total ,
            address: address , 
            promocode: promocode , 
            discount_amount: discount_amount , 
            discount_pr: discount_pr , 
            tax : tax,
            tax_amount : tax_amount,
            delivery_charge : delivery_charge ,
            notes : notes,
            order_type : order_type,
            lat : lat,
            lang : lang,
            building : building,
            landmark : landmark,
            pincode : pincode,
            city : city,
            state : state,
            country : country,
            stripeToken : token,
            ordered_date : ordered_date,
        }, 
        method: 'POST',
        success: function(response) {
            $('#preloader').hide();
            if (response.status == 1) {
                window.location.href = "{{ URL::to('/orders') }}";
            } else {
                toast.error("Somethingm went wrong");
            }
        },
        error: function(error) {
            toast.error("Somethingm went wrong");
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
</script>