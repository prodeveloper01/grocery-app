@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">{{$getitem->item_name}}</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#">Product</a></li>
						<li class="breadcrumb-item active"><a href="#">{{$getitem->category_name}}</a></li>
					  </ol>
					</nav>
				</div>
			</div>
			
		</div>
	</div>
</div>
<!-- =========================== Breadcrumbs =================================== -->

<?php 
$strings = array('#D0EEF9','#FEEBD1','#FEE3DC','#F0D7F9','#EAF8ED');
$count = count($strings);
?>

<!-- =========================== Product Detail =================================== -->
<section>
	<div class="container">
		<div class="row">
		
			<div class="col-lg-6 col-md-12 col-sm-12">
				<div class="sp-loading"><br>LOADING IMAGES</div>
				<div class="sp-wrap">
					@foreach ($getimages as $images)
					<a href="{{$images->image }}"><img src="{{$images->image }}" alt=""></a>
					@endforeach
				</div>
			</div>
			
			<div class="col-lg-6 col-md-12 col-sm-12">
				<div class="woo_pr_detail">
					
					<div class="woo_cats_wrps">
						<a href="#" class="woo_pr_cats">{{$getitem->category_name}}</a>
					</div>
					<h2 class="woo_pr_title">{{$getitem->item_name}}</h2>
					
					<div class="woo_pr_short_desc">
						<p>{{$getitem->item_description}}</p>
					</div>
					
					<div class="woo_pr_price">
						@foreach ($getitem->variation as $key => $value)
							@if ($value->stock != 0)
								<h3>{{$getdata->currency}}{{number_format($value->price, 2)}}</h3>
								@break
							@endif
						@endforeach
					</div>
					
					<!-- Short Info -->
					<div class="pr_info_prefix grocery_style mb-3">
						
						<!-- First -->
						<div class="cart_sku_preflix">
							<div class="sku_preflix_first">
								<strong>Brand:</strong>
							</div>
							<div class="sku_preflix_last">
								{{$getitem->brand}}
							</div>
						</div>
						
						<!-- First -->
						<div class="cart_sku_preflix">
							<div class="sku_preflix_first">
								<strong>Manufacturer:</strong>
							</div>
							<div class="sku_preflix_last">
								{{$getitem->manufacturer}}
							</div>
						</div>
						
						<!-- First -->
						<div class="cart_sku_preflix">
							<div class="sku_preflix_first">
								<strong>Country Origin:</strong>
							</div>
							<div class="sku_preflix_last">
								{{$getitem->country_origin}}
							</div>
						</div>
						
						<!-- First -->
						<div class="cart_sku_preflix">
							<div class="sku_preflix_first">
								<strong>Ingredient Type:</strong>
							</div>
							<div class="sku_preflix_last">
								{{$getitem->ingredient_type}}
							</div>
						</div>
					</div>
					<input type="hidden" name="item_id" id="item_id" value="{{$getitem->id}}">
					@foreach($getitem['variation'] as $key => $variation)
					<div class="custom-varient custom-size">
						<input type="radio" variation-id="{{$variation->id}}" class="custom-control-input" name="variation" id="variation-{{$key}}-{{$variation->item_id}}" value="{{$variation->weight}} - {{$variation->price}}">
						<label class="custom-control-label" for="variation-{{$key}}-{{$variation->item_id}}">{{$variation->weight}} - {{$getdata->currency}}{{$variation->price}}</label>
					</div>
					@endforeach

					<div class="woo_btn_action">
						@if (@Auth::user()->id != "")
							<div class="col-12 col-lg-auto">
								<button class="btn btn-block btn-dark mb-2" onclick="AddtoCart('item')">Add to Cart<i class="ti-shopping-cart-full ml-2"></i></button>
							</div>
							@if ($getitem->is_favorite != 1)
								<div class="col-12 col-lg-auto">
									<button class="btn btn-gray btn-block mb-2" data-toggle="button" onclick="MakeFavorite('{{$getitem->id}}','{{@Auth::user()->id}}')">Wishlist <i class="ti-heart ml-2"></i></button>
								</div>
							@endif
							
						@else
							<div class="col-12 col-lg-auto">
								<a href="{{URL::to('/signin')}}" class="btn btn-block btn-dark mb-2">Add to Cart <i class="ti-shopping-cart-full ml-2"></i></a>
							</div>
							<div class="col-12 col-lg-auto">
								<a href="{{URL::to('/signin')}}" class="btn btn-gray btn-block mb-2">Wishlist <i class="ti-heart ml-2"></i></a>
							</div>
						@endif
						
					</div>
					
				</div>
			</div>
			
		</div>
		
	</div>
</section>
<!-- =========================== Product Detail =================================== -->

<!-- =========================== `ted Products =================================== -->
<section class="gray">
	<div class="container">
		
		<div class="row">
			<div class="col-lg-12 col-md-12 mb-2">
				<h4 class="mb-0">Related Products</h4>
			</div>						
		</div>
		
		<div class="row">
		
			<div class="col-lg-12 col-md-12">
				<div class="owl-carousel products-slider owl-theme">
					@foreach ($relatedproduct as $key => $item)
					<!-- Single Item -->
					<div class="item">
						<div class="woo_product_grid" style="background-color: <?php echo $strings[$key % $count]; ?>">
							<div class="woo_product_thumb">
								<img src='{{$item["itemimage"]->image }}' class="img-fluid" alt="" />
							</div>
							<div class="woo_product_caption center">
								<div class="woo_title">
									<h4 class="woo_pro_title"><a href="#">{{ Str::limit($item->item_name, 35) }}</a></h4>
								</div>
								<div class="woo_price">
									@foreach ($item->variation as $key => $value)
										@if ($value->stock != 0)
											<h6>{{$getdata->currency}}{{$value->price}}</h6>
											@break
										@endif
									@endforeach
								</div>
							</div>
							<div class="woo_product_cart hover">
								<ul>
									<li><a href="{{URL::to('product-details/'.$item->slug)}}" class="woo_cart_btn btn_cart"><i class="ti-eye"></i></a></li>
									<li><a href="javascript:void(0)" onclick="GetProductOverview('{{$item->id}}')" class="woo_cart_btn btn_view"><i class="ti-shopping-cart"></i></a></li>
									@if ($item->is_favorite == 1)
										<li><a href="javascript:void();" class="woo_cart_btn btn_save"><i class="fa fa-heart" aria-hidden="true"></i></a></li>
									@else
									    <li><a href="javascript:void();" onclick="MakeFavorite('{{$item->id}}','{{@Auth::user()->id}}')" class="woo_cart_btn btn_save"><i class="ti-heart"></i></a></li>
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
<!-- =========================== Related Products =================================== -->
<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')