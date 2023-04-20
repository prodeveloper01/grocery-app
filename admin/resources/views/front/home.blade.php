@include('front.theme.header')

<!-- ======================== Offer Banner Start ==================== -->
<section class="offer-banner-wrap gray pt-4">
	<div class="container-fluid">
		<div class="row">
			<div class="col-lg-12 col-md-12">
				<div class="owl-carousel banner-offers owl-theme">
					@foreach ($getbanner as $banner)
					<!-- Single Item -->
					<div class="item">
						<div class="offer_item">
							<div class="offer_item_thumb">
								<div class="offer-overlay"></div>
								@if ($banner->type == "category")
			                        <a href="{{URL::to('products/'.$banner->cat_slug)}}">
			                    @else
			                        <a href="{{URL::to('product-details/'.$banner->item_slug)}}">
			                    @endif
								<img src='{!! asset("public/images/banner/".$banner->image) !!}' alt="">
								</a>
							</div>
						</div>
					</div>
					@endforeach
				</div>
			</div>
		</div>
	</div>
	<div class="ht-25"></div>
</section>
<div class="clearfix"></div>
<!-- ======================== Offer Banner End ==================== -->
<?php 
$strings = array('#D0EEF9','#FEEBD1','#FEE3DC','#F0D7F9','#EAF8ED');
$count = count($strings);
?>
<!-- ======================== Choose Category Start ==================== -->
<section class="pt-0 overlio">
		<div class="container">
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="sec-heading-flex pl-2 pr-2">
					<div class="sec-heading-flex-one">
						<h2>Categories</h2>
					</div>
					<div class="sec-heading-flex-last">
						<a href="{{URL::to('/category')}}" class="btn btn-theme">View More<i class="ti-arrow-right ml-2"></i></a>
					</div>
				</div>
			</div>
		</div>
				
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="owl-carousel category-slider owl-theme">
					@foreach ($getcategory as $key => $category)
					
					<!-- Single Item -->
					<div class="item">
						<div class="woo_category_box border_style rounded" style="background-color: <?php echo $strings[$key % $count]; ?>">
							<div class="woo_cat_thumb">
								<a href="{{URL::to('products/'.$category->slug)}}"><img src='{!! asset("public/images/category/".$category->image) !!}' class="img-fluid" alt="" /></a>
							</div>
							<div class="woo_cat_caption">
								<h4><a href="{{URL::to('products/'.$category->slug)}}">{{$category->category_name}}</a></h4>
							</div>
						</div>
					</div>
					@endforeach
				</div>
			</div>
		</div>
		
	</div>
</section>
<div class="clearfix"></div>
<!-- ======================== Choose Category End ==================== -->

<!-- ======================== Fresh Vegetables Start ==================== -->
<section class="pt-0">
	<div class="container">
		
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="sec-heading-flex pl-2 pr-2">
					<div class="sec-heading-flex-one">
						<h2>Latest Products</h2>
					</div>
					<div class="sec-heading-flex-last">
						<a href="{{URL::to('/latest-products')}}" class="btn btn-theme">View More<i class="ti-arrow-right ml-2"></i></a>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-lg-12 col-md-12">
				<div class="owl-carousel products-slider owl-theme">
					@foreach($getitem as $key => $item)
					<!-- Single Item -->
					<div class="item">
						<div class="woo_product_grid" style="background-color: <?php echo $strings[$key % $count]; ?>">
							<div class="woo_product_thumb">
								<img src='{{$item["itemimage"]->image }}' class="img-fluid" alt="" />
							</div>
							<div class="woo_product_caption center">
								<div class="woo_title">
									<h4 class="woo_pro_title"><a href="javascript:void();">{{ Str::limit($item->item_name, 35) }}</a></h4>
								</div>
								<div class="woo_price">
							        @foreach ($item->variation as $key => $value)
							        	@if ($value->stock != 0)
							        		<h6>{{$getdata->currency}}{{number_format($value->price, 2)}}</h6>
							        		@break
							        	@endif
							        @endforeach
								</div>
							</div>
							<div class="woo_product_cart hover">
								<ul>
									<li><a href="{{URL::to('product-details/'.$item->slug)}}" class="woo_cart_btn btn_cart"><i class="ti-eye"></i></a></li>
									<li><a href="javascript:void(0)" onclick="GetProductOverview('{{$item->id}}')" class="woo_cart_btn btn_view"><i class="ti-shopping-cart"></i></a></li>
									@if (@Auth::user()->id != "" && @Auth::user()->type != "1")
										@if ($item->is_favorite == 1)
											<li><a href="javascript:void();" class="woo_cart_btn btn_save"><i class="fa fa-heart" aria-hidden="true"></i></a></li>
										@else
										    <li><a href="javascript:void();" onclick="MakeFavorite('{{$item->id}}','{{@Auth::user()->id}}')" class="woo_cart_btn btn_save"><i class="ti-heart"></i></a></li>
										@endif
									@else
										<li><a href="{{URL::to('/signin')}}" class="woo_cart_btn btn_save"><i class="ti-heart" aria-hidden="true"></i></a></li>
									@endif
									
								</ul>
							</div>	
						</div>
					</div>
					@endforeach
				</div>
			</div>
		</div>
	</div>
</section>
<div class="clearfix"></div>
<!-- ======================== Fresh Vegetables End ==================== -->

<!-- ======================== Fresh & Fast Fruits Start ==================== -->
<section class="pt-0">
	<div class="container">
		
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="sec-heading-flex pl-2 pr-2">
					<div class="sec-heading-flex-one">
						<h2>Explore Products</h2>
					</div>
					<div class="sec-heading-flex-last">
						<a href="{{URL::to('/explore-products')}}" class="btn btn-theme">View More<i class="ti-arrow-right ml-2"></i></a>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-lg-12 col-md-12">
				<div class="owl-carousel products-slider owl-theme">
					@foreach($exploredata as $key => $item)
					<!-- Single Item -->
					<div class="item">
						<div class="woo_product_grid" style="background-color: <?php echo $strings[$key % $count]; ?>">
							<div class="woo_product_thumb">
								<img src='{{$item["itemimage"]->image }}' class="img-fluid" alt="" />
							</div>
							<div class="woo_product_caption center">
								<div class="woo_title">
									<h4 class="woo_pro_title"><a href="javascript:void();">{{ Str::limit($item->item_name, 35) }}</a></h4>
								</div>
								<div class="woo_price">
									@foreach ($item->variation as $key => $value)
										@if ($value->stock != 0)
											<h6>{{$getdata->currency}}{{number_format($value->price, 2)}}</h6>
											@break
										@endif
									@endforeach
								</div>
							</div>
							<div class="woo_product_cart hover">
								<ul>
									<li><a href="{{URL::to('product-details/'.$item->slug)}}" class="woo_cart_btn btn_cart"><i class="ti-eye"></i></a></li>
									<li><a href="javascript:void(0)" onclick="GetProductOverview('{{$item->id}}')" class="woo_cart_btn btn_view"><i class="ti-shopping-cart"></i></a></li>
									@if (@Auth::user()->id != "" && @Auth::user()->type != "1")
										@if ($item->is_favorite == 1)
											<li><a href="javascript:void();" class="woo_cart_btn btn_save"><i class="fa fa-heart" aria-hidden="true"></i></a></li>
										@else
										    <li><a href="javascript:void();" onclick="MakeFavorite('{{$item->id}}','{{@Auth::user()->id}}')" class="woo_cart_btn btn_save"><i class="ti-heart"></i></a></li>
										@endif
									@else
										<li><a href="{{URL::to('/signin')}}" class="woo_cart_btn btn_save"><i class="ti-heart" aria-hidden="true"></i></a></li>
									@endif
								</ul>
							</div>
						</div>
					</div>
					@endforeach
				</div>
			</div>
		</div>
	</div>
</section>
<div class="clearfix"></div>
<!-- ======================== Fresh & Fast Fruits End ==================== -->

<!-- ======================== About Start ==================== -->
<section>
	<div class="container">
		<div class="row">
		
			<div class="col-lg-5 col-md-6 col-sm-12">
				<h2>About us</h2>
				<p>{!! \Illuminate\Support\Str::limit(htmlspecialchars($getabout->about_content, ENT_QUOTES, 'UTF-8'), $limit = 500, $end = '...') !!}</p>
				<a href="{{URL::to('/about')}}" class="btn btn-theme mt-2">More</a>
			</div>
			
			<div class="col-lg-6 col-md-6 col-sm-12 ml-auto">
				<img class="pro_img img-fluid w100" src='{!! asset("public/images/about/".$getabout->image) !!}' width="500px" height="500px">
			</div>
			
		</div>
	</div>
</section>
<!-- ======================== About End ==================== -->


<div class="clearfix"></div>

<section class="gray">
	<div class="container">
		
		<div class="row justify-content-center">
			<div class="col-lg-10 col-md-10 col-sm-12">
				<div class="sec-heading">
					<div class="sec-heading center">
						<h2>Our Reviews</h2>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-lg-12 col-md-12">
				
				<div class="customers-carousel owl-carousel owl-theme products-slider " id="customers-carousel">
					@foreach($getreview as $review)
					<!-- single testimonial -->
					<div class="single_customers_box">
						<div class="row justify-content-center">
							<div class="col-lg-7 col-md-7">
								<div class="single_customers_wraps">
									<div class="single_customers_caption">
										<div class="quote_icon_2"><i class="fas fa-quote-right"></i></div>
										<p class="customers_description">{{$review->comment}}</p>
										<div class="review_author_box">
											<div class="reviews_img">
												<img src='{!! asset("public/images/profile/".$review["users"]->profile_image) !!}' class="img-fluid" alt="" />	
											</div>
											<div class="reviews_caption">
												<h4 class="testi2_title">{{$review['users']->name}}</h4>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					@endforeach
				</div>
				
			</div>						
		</div>
	</div>
</section>
<!-- ======================== Fresh Vegetables & Fruits End ==================== -->

<section>
	<div class="container">
		<div class="row">			
			<div class="col-lg-12 col-md-12">
				<div class="contact-form">
					<h2 class="sec-heading center">Contact us</h2>
					<form id="contactform" method="post">
						{{csrf_field()}}
						<div class="form-row">
							<div class="form-group col-md-6">
							  <label>First Name</label>
							  <input type="email" class="form-control" name="firstname" id="firstname" placeholder="First Name">
							</div>

							<div class="form-group col-md-6">
							  <label>Last Name</label>
							  <input type="email" class="form-control" name="lastname" id="lastname" placeholder="Last Name">
							</div>
						</div>
						
						<div class="form-group col-lg-12 col-md-12">
							<label>Email</label>
							<input type="email" class="form-control" name="email" id="email" placeholder="Email">
						</div>
						
						<div class="form-group col-lg-12 col-md-12">
							<label>Message</label>
							<textarea class="form-control" placeholder="Type Here..." name="message" id="message"></textarea>
						</div>
						
						<div class="form-group col-lg-12 col-md-12">
						<button type="button" class="btn btn-primary" onclick="contact()">Send Request</button>
						</div>
						
					</form>
				</div>
			</div>
			
		</div>
	</div>
</section>
@include('front.theme.footer')