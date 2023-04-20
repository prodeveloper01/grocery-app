<div class="w3-ch-sideBar w3-bar-block w3-card-2 w3-animate-right" style="display:none;right:0;" id="rightMenu">

	<div class="rightMenu-scroll">

		<h4 class="cart_heading">Your cart</h4>

		<button onclick="closeRightMenu()" class="w3-bar-item w3-button w3-large"><i class="ti-close"></i></button>

		@if (@Auth::user()->id != "" && @Auth::user()->type != "1")

			<div class="right-ch-sideBar" id="side-scroll">

										

				<div class="cart_select_items">

					<!-- Single Item -->

					@foreach ($cart as $cartitems)

					<div class="product cart_selected_single">

						<div class="cart_selected_single_thumb">

							<a href="#"><img src="{{$cartitems['itemimage']->image}}" class="img-fluid" alt="" /></a>

						</div>

						<div class="cart_selected_single_caption">

							<h4 class="product_title">{{$cartitems->item_name}}</h4>

							<span class="numberof_item mt-2"><b>{{$cartitems->weight}}</b> {{$getdata->currency}}{{number_format($cartitems->qty * $cartitems->price, 2)}}</span>

							<a href="javascript::void()" onclick="DeleteItem('{{$cartitems->id}}','1')" class="text-danger">Remove</a>

						</div>

					</div>

					<?php

					$data[] = array(

					    "total_price" => $cartitems->qty * $cartitems->price

					);

					$order_total = array_sum(array_column(@$data, 'total_price'));

					?>

					@endforeach



				</div>

				

				<div class="cart_subtotal">

					<h6>Subtotal<span class="theme-cl">{{$getdata->currency}}{{number_format(@$order_total, 2)}}</span></h6>

				</div>

				

				<div class="cart_action">

					<ul>

						<li><a href="{{URL::to('/cart')}}" class="btn btn-go-cart btn-theme">View/Edit Cart</a></li>

					</ul>

				</div>

				

			</div>

		@else

			<div class="right-ch-sideBar" id="side-scroll">

										

				<div class="cart_action">

					<ul>

						<li><a href="{{URL::to('/signin')}}" class="btn btn-go-cart btn-theme">Login</a></li>

					</ul>

				</div>

				

			</div>

		@endif

		

	</div>

</div>