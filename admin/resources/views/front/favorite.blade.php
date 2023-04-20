@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">My Wishlist</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">My Wishlist</li>
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
		
			@include('front.theme.sidebar')
			
			<div class="col-lg-8 col-md-9">
				<div class="row product">

					@if (count($favorite) == 0)
					    <p>No Data found</p>
					@else 
					    @foreach ($favorite as $key => $item)
				
					<!-- Single Item -->
					<div class="col-lg-4 col-md-4 col-sm-4">
						<div class="woo_product_grid no-hover" style="background-color: <?php echo $strings[$key % $count]; ?>">
							<div class="woo_product_thumb">
								<a href="{{URL::to('product-details/'.$item->slug)}}"><img src='{{$item["itemimage"]->image }}' class="img-fluid" alt="" /></a>
							</div>
							<div class="woo_product_caption center">
								<div class="woo_title">
									<h4 class="woo_pro_title mt-2"><a href="{{URL::to('product-details/'.$item->slug)}}">{{ Str::limit($item->item_name, 35) }}</a></h4>
								</div>
								<div class="woo_price">
									<h6 style="font-family: 'CinzelDecorative' !important">
										@foreach ($item->variation as $key => $value)
											@if ($value->stock != 0)
												<h6>{{$getdata->currency}}{{number_format($value->price, 2)}}</h6>
												@break
											@endif
										@endforeach
									</h6>
								</div>

								<div class="woo_price">
									<span class="woo_pr_trending mt-2" onclick="Unfavorite('{{$item->id}}','{{Auth::user()->id}}')">Remove from Wishlist <i class="fas fa-heart i ml-2"></i></span>	
								</div>

							</div>							
						</div>
					</div>
					    @endforeach
					@endif
				
				</div>

				<div class="row">
					<div class="col-lg-12">
						<nav aria-label="Page navigation example">
							@if ($favorite->lastPage() > 1)
						  <ul class="pagination">
							<li class="page-item left {{ ($favorite->currentPage() == 1) ? ' disabled' : '' }}">
							  <a class="page-link" href="{{ $favorite->url(1) }}" aria-label="Previous">
								<span aria-hidden="true"><i class="ti-arrow-left mr-1"></i>Prev</span>
							  </a>
							</li>
							@for ($i = 1; $i <= $favorite->lastPage(); $i++)
							<li class="page-item {{ ($favorite->currentPage() == $i) ? ' active' : '' }}"><a class="page-link" href="{{ $favorite->url($i) }}">{{ $i }}</a></li>
							@endfor
							<li class="page-item right {{ ($favorite->currentPage() == $favorite->lastPage()) ? ' disabled' : '' }}">
							  <a class="page-link" href="{{ $favorite->url($favorite->lastPage()) }}" aria-label="Previous">
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
</section>
<!-- =========================== Account Settings =================================== -->
<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')