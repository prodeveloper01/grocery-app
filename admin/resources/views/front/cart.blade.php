@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">My Cart</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">My Cart</li>
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
		@if (count($cart) == 0)
		    <p>No Data found</p>
		@else 
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
								<td>
									<div class="pro-add">
										<div class="value-button sub" id="decrease" onclick="qtyupdate('{{$cartitems->id}}','decreaseValue')" value="Decrease Value">
	                                        <i class="fa fa-minus-circle"></i>
	                                    </div>
											<input type="number" id="number_{{$cartitems->id}}" name="number" value="{{$cartitems->qty}}" readonly="" min="1" max="10" />
										<div class="value-button add" id="increase" onclick="qtyupdate('{{$cartitems->id}}','increase')" value="Increase Value">
										    <i class="fa fa-plus-circle"></i>
										</div>
									</div>
								</td>
								<td>
									<div class="tbl_pr_action">
										<h5 class="tbl_total_price">{{$getdata->currency}}{{number_format($cartitems->qty * $cartitems->price, 2)}}</h5>
										<a href="javascript:void(0);" class="tbl_remove" onclick="DeleteItem('{{$cartitems->id}}','1')"><i class="ti-close"></i></a>
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
				
				<!-- Coupon Box -->
				<div class="row align-items-end justify-content-between mb-10 mb-md-0">
					<div class="col-12 col-md-7">
						@if (Session::has('offer_amount'))
						<!-- Coupon -->
						<form class="mb-7 mb-md-0">
							<div class="col">
								<label class="font-size-sm font-weight-bold">Coupon code:</label>
							</div>
							<div class="row form-row">
								<div class="col">
									<!-- Input -->
									<input class="form-control form-control-sm" name="removepromocode" id="removepromocode" type="text" autocomplete="off" placeholder="Enter coupon code" value="{{Session::get('offer_code')}}">
								</div>
								<div class="col-auto">
									<!-- Button -->
									<button class="btn btn-dark" id="ajaxRemove" type="submit">Remove</button>
								</div>
							</div>
						</form>
						@else
						<!-- Coupon -->
						<form class="mb-7 mb-md-0">
							<div class="col">
								<label class="font-size-sm font-weight-bold">Promocode</label>
							</div>
							<div class="row form-row">
								<div class="col">
									<!-- Input -->
									<input class="form-control form-control-sm" name="promocode" id="promocode" type="text" autocomplete="off" placeholder="Promocode">
									<p data-toggle="modal" data-target="#staticBackdrop" style="color: #ffc107">Select Promocode</p>
								</div>
								<div class="col-auto">
									<!-- Button -->
									<button class="btn btn-dark" id="ajaxSubmit">Apply</button>
								</div>
							</div>
						</form>
						@endif
					</div>
				</div>
				<!-- Coupon Box -->
				
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
							<li class="list-group-item d-flex delivery_charge_section">
								<span>Delivery Charge</span> <span class="ml-auto font-size-sm">{{$getdata->currency}}{{number_format($getdata->delivery_charge, 2)}}</span>
							</li>

							@if (Session::has('offer_amount'))
								<li class="list-group-item d-flex">
									<span>Discount ({{Session::get('offer_code')}})</span> <span class="ml-auto font-size-sm">-{{$getdata->currency}}{{number_format($subtotal*Session::get('offer_amount')/100, 2)}}</span>
								</li>
							@endif

							@if (Session::has('offer_amount'))
								<li class="list-group-item d-flex font-size-lg font-weight-bold">
									<span>Total</span> <span class="ml-auto font-size-sm" id="total_amount">{{$getdata->currency}}{{number_format($subtotal + $getdata->delivery_charge + $taxprice-$subtotal*Session::get('offer_amount')/100, 2)}}</span>
								</li>
							@else
							    <li class="list-group-item d-flex font-size-lg font-weight-bold">
							    	<span>Total</span> <span class="ml-auto font-size-sm" id="total_amount">{{$getdata->currency}}{{number_format($subtotal + $taxprice + $getdata->delivery_charge, 2)}}</span>
							    </li>
							@endif
						</ul>
					</div>
					<div class="cart-delivery-type open">
					    <label for="cart-delivery">
					        <input type="radio" name="cart_delivery" id="cart-delivery" checked value="1">
					        <div class="cart-delivery-type-box">
					            <img src="{!! asset('public/front/img/pickup-truck.png') !!}" height="40" width="40" alt="">                                   
					            <p>Delivery</p>
					        </div>
					    </label>
					    <label for="cart-pickup">
					        <input type="radio" name="cart_delivery" id="cart-pickup" value="2">
					        <div class="cart-delivery-type-box">
					            <img src="{!! asset('public/front/img/delivery.png') !!}" height="40" width="40" alt="">
					            <p>Pickup</p>
					        </div>
					    </label>
					</div>
					<input type="hidden" name="subtotal" id="subtotal" value="{{$subtotal}}">
					<input type="hidden" name="tax_amount" id="tax_amount" value="{{$taxprice}}">
					<input type="hidden" name="email" id="email" value="{{Auth::user()->email}}">
					<input type="hidden" name="delivery_charge" id="delivery_charge" value="{{$getdata->delivery_charge}}">

					@if (Session::has('offer_amount'))
					    <input type="hidden" name="discount_amount" id="discount_amount" value="{{$subtotal*Session::get('offer_amount')/100}}">
					@else
					    <input type="hidden" name="discount_amount" id="discount_amount" value="">
					@endif

					<div class="col-lg-12 col-md-12 mt-3">
						<div class="form-group">
							<label>Order Date</label>
							<input type="date" class="form-control" name="ordered_date" id="ordered_date" value="{{date('Y-m-d')}}">
						</div>
					</div>

					<div class="select_add col-md-12 mt-3">
					    @if (!$addressdata->isEmpty())
					        <p data-toggle="modal" data-target="#select_address" style="width: 50%;" class="btn btn-block btn-dark mb-2">Select Address</p>
					    @else
					        <a href="{{URL::to('/address')}}" style="width: 50%;" class="btn btn-block btn-dark mb-2">Add Address</a>
					    @endif
					</div>

					@if (env('Environment') == 'sendbox')
						<div id="address-data" class="mt-3">

							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<label>Address</label>
									<input type="text" class="form-control" name="address" id="address" placeholder="Address" autocomplete="off" value="New York, NY, USA" required="" readonly="" autocomplete="on">
									<input type="hidden" id="lat" name="lat" value="40.7127753"/>
									<input type="hidden" id="lang" name="lang" value="-74.0059728"/>
									<input type="hidden" id="city" name="city" placeholder="city" value="New York"/> 
									<input type="hidden" id="state" name="state" placeholder="state" value="NY"/> 
									<input type="hidden" id="country" name="country" placeholder="country" value="US"/>
								</div>
							</div>
							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<label>Pincode</label>
									<input type="text" class="form-control" name="pincode" id="pincode" readonly="" value="10001" placeholder="Pincode">
								</div>
							</div>
							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<label>Door / Flat no.</label>
									<input type="text" class="form-control" name="building" id="building"  value="4043" readonly="" placeholder="Door / Flat no.">
								</div>
							</div>
							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<label>Landmark</label>
									<input type="text" class="form-control" name="landmark" value="Central Park" readonly="" id="landmark" placeholder="Landmark">
								</div>
							</div>						
						</div>
					@else
						<div id="address-data" class="mt-3">
							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<input type="text" class="form-control" name="address" id="address" placeholder="Address"readonly="">
									<input type="hidden" id="lat" name="lat"/>
									<input type="hidden" id="lang" name="lang"/>
									<input type="hidden" id="city" name="city" placeholder="city"/> 
									<input type="hidden" id="state" name="state" placeholder="state"/> 
									<input type="hidden" id="country" name="country" placeholder="country"/>
								</div>
							</div>
							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<input type="text" class="form-control" name="pincode" id="pincode" placeholder="Pincode" readonly="">
								</div>
							</div>
							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<input type="text" class="form-control" name="building" id="building" placeholder="Door / Flat no." readonly="">
								</div>
							</div>
							<div class="col-lg-12 col-md-12">
								<div class="form-group">
									<input type="text" class="form-control" name="landmark" id="landmark" placeholder="Landmark" readonly="">
								</div>
							</div>						
						</div>
					@endif
					
					<div class="col-lg-12 col-md-12">
						<div class="form-group">
							<label>Notes</label>
							<textarea class="form-control" name="notes" id="notes" placeholder="Order Notes"></textarea>
						</div>
					</div>

				</div>
				<a class="btn btn-block btn-dark mb-2" onclick="Checkout()" style="color: #fff;">Proceed to Checkout</a>
				<a class="px-0 text-body"><i class="ti-back-left mr-2"></i> Continue Shopping</a>
			</div>
			
			
		</div>
		@endif
	</div>
</section>
<!-- =========================== Cart =================================== -->

<!-- Modal -->
<div class="promo-modal modal fade" id="staticBackdrop" data-backdrop="static" data-keyboard="false" tabindex="-1"
    role="dialog" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-head">
                <h4>Select Promocode</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                @foreach ($getpromocode as $promocode)
                <div class="promo-box">
                    <button class="btn btn-dark" data-id="{{$promocode->offer_code}}">Copy</button>
                    <p class="promo-title">{{$promocode->offer_name}}</p>
                    <p class="promo-code-here">Code :: <span>{{$promocode->offer_code}}</span></p>
                    <small>{{$promocode->description}}</small>
                </div>
                @endforeach
            </div>
        </div>
    </div>
</div>

<!-- Address Modal -->
<div class="promo-modal modal fade" id="select_address" data-backdrop="static" data-keyboard="false" tabindex="-1"
    role="dialog" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-head">
                <h4>Select Address</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                @foreach ($addressdata as $address)
                <div class="promo-box">
                    <button class="btn btn-select btn-dark" style="padding: 0px 18px;" data-address="{{$address->address}}" data-postal_code="{{$address->pincode}}" data-building="{{$address->building}}" data-landmark="{{$address->landmark}}" data-lat="{{$address->lat}}" data-lang="{{$address->lang}}" data-city="{{$address->city}}" data-state="{{$address->state}}" data-country="{{$address->country}}">Select</button>
                    <p class="promo-code-here">
                        Type :: 
                        @if ($address->address_type == 1)
                            Home
                        @elseif ($address->address_type == 2)
                            Work
                        @else
                            Other
                        @endif
                    </p>
                    <p class="promo-title">{{$address->address}}</p>
                    <small>{{$address->landmark}}, {{$address->building}}, {{$address->pincode}}</small>
                </div>
                @endforeach
            </div>
        </div>
    </div>
</div>

<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')
<script>
	$(document).ready(function() {
		jQuery('#ajaxSubmit').click(function(e){
		    e.preventDefault();
		    $.ajaxSetup({
		        headers: {
		            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
		        },
		    });
		    $('#preloader').show();
	    	jQuery.ajax({
		        url: "{{ URL::to('cart/applypromocode') }}",
		        method: 'post',
		        data: {
		            promocode: jQuery('#promocode').val()
		        },
		        success: function(response){
	            $('#preloader').hide();

	            if (response.status == 1) {
	            	$('.offer_amount').css("display","flex");
	            	location.reload();
	            	// toast.success(response.message);
	            } else {
	            	toast.error(response.message);
	            }
	        }});
	    });

	    jQuery('#ajaxRemove').click(function(e){
	        e.preventDefault();
	        $.ajaxSetup({
	            headers: {
	                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
	            },
	        });
	        $('#preloader').show();
	        jQuery.ajax({
            url: "{{ URL::to('cart/removepromocode') }}",
            method: 'post',
            data: {
                promocode: jQuery('#promocode').val()
            },
            success: function(response){

                $('#preloader').hide();
                if (response.status == 1) {
                    location.reload();
                } else {
                	toast.error(response.message);
                }
            }});
        });

	    $("input[name$='cart_delivery']").click(function() {
	        var type = $(this).val();

	        if (type == 1) {
	            $(".delivery_charge_section").show();
	            $("#address-data").show();
	            $(".select_add").show();

	            var subtotal = parseFloat($('#subtotal').val());
	            var delivery_charge = parseFloat($('#delivery_charge').val());
	            var tax_amount =  parseFloat($('#tax_amount').val());
	            var discount_amount =  parseFloat($('#discount_amount').val());

	            if (isNaN(discount_amount)) {
	                $('#total_amount').text('{{$getdata->currency}}'+(subtotal+tax_amount+delivery_charge).toFixed(2));
	            } else {
	                $('#total_amount').text('{{$getdata->currency}}'+(subtotal+tax_amount+delivery_charge-discount_amount).toFixed(2));
	            }

	        } else {
	            $('.delivery_charge_section').attr('style','display:none !important');
	            $("#address-data").hide();
	            $(".select_add").hide();

	            var subtotal = parseFloat($('#subtotal').val());
	            var tax_amount =  parseFloat($('#tax_amount').val());
	            var discount_amount =  parseFloat($('#discount_amount').val());
	            if (isNaN(discount_amount)) {
	            	$('#total_amount').text('{{$getdata->currency}}'+(subtotal+tax_amount).toFixed(2));
	            } else {
	            	$('#total_amount').text('{{$getdata->currency}}'+(subtotal+tax_amount-discount_amount).toFixed(2));
	            }
	        }
	    });
	});

	$('body').on('click','.btn-dark',function(e) {
	            
	    var text = $(this).attr('data-id');
	        $('#promocode').val(text);
	        $('#staticBackdrop').modal('hide');	    
	});

	var SITEURL = '{{URL::to('')}}';
	function Checkout() {
	    var order_type = $("input:radio[name=cart_delivery]:checked").val();
	    var address = $('#address').val();
	    var pincode = $('#pincode').val();
	    var building = $('#building').val();
	    var landmark = $('#landmark').val();
	    var notes = $('#notes').val();
	    var lat = $('#lat').val();
	    var lang = $('#lang').val();
	    var city = $('#city').val();
	    var country = $('#country').val();
	    var state = $('#state').val();
	    var ordered_date = $('#ordered_date').val();

	    $('#preloader').show();
	    $.ajax({
	        headers: {
	            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
	        },
	        url:"{{ URL::to('cart/checkoutdata') }}",
	        data: {
	            order_type : order_type,
	            address : address,
	            pincode : pincode,
	            building : building,
	            landmark : landmark,
	            notes : notes,
	            lat : lat,
	            lang : lang,
	            city : city,
	            state : state,
	            country : country,
	            ordered_date : ordered_date,
	        }, 
	        method: 'POST',
	        success: function(response) {
	            $('#preloader').hide();
	            if (response.status == 1) {
	                window.location.href = SITEURL + '/cart/checkout';
	            } else {
	                toast.error(response.message);
	            }
	        },
	        error: function(error) {
	        	toast.error("Something went wrong");
	        }
	    });
	}

	$('body').on('click','.btn-select',function(e) {
            
	    var address = $(this).attr('data-address');
	    var postal_code = $(this).attr('data-postal_code');
	    var building = $(this).attr('data-building');
	    var landmark = $(this).attr('data-landmark');
	    var lat = $(this).attr('data-lat');
	    var lang = $(this).attr('data-lang');
	    var city = $(this).attr('data-city');
	    var state = $(this).attr('data-state');
	    var country = $(this).attr('data-country');

	        $('#address').val(address);
	        $('#pincode').val(postal_code);
	        $('#building').val(building);
	        $('#landmark').val(landmark);
	        $('#lat').val(lat);
	        $('#lang').val(lang);
	        $('#city').val(city);
	        $('#state').val(state);
	        $('#country').val(country);
	        $('#select_address').modal('hide');    
	});

	$(function(){
	    var dtToday = new Date();
	    
	    var month = dtToday.getMonth() + 1;
	    var day = dtToday.getDate();
	    var year = dtToday.getFullYear();
	    if(month < 10)
	        month = '0' + month.toString();
	    if(day < 10)
	        day = '0' + day.toString();
	    
	    var maxDate = year + '-' + month + '-' + day;

	    // or instead:
	    // var maxDate = dtToday.toISOString().substr(0, 10);

	    $('#ordered_date').attr('min', maxDate);
	});
</script>