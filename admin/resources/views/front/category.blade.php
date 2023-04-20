@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">Categories</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">Categories</li>
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

<!-- =========================== Search Products =================================== -->
<section>
	<div class="container">
		
		<div class="row">
			
			<div class="col-lg-12 col-md-12">
								
				<!-- row -->
				<div class="row">						
					<ul class="product grid-5">
						@foreach($getcategory as $key => $category)
						<!-- Single Item -->
						<li>
							<div class="woo_product_grid no-hover" style="background-color: <?php echo $strings[$key % $count]; ?>">
								<div class="woo_cat_thumb">
									<a href="{{URL::to('products/'.$category->slug)}}" style="text-align: center; padding-top: 20px;">
										<img src='{!! asset("public/images/category/".$category->image) !!}' class="img-fluid" alt=""  style="max-width: 80px" />
									</a>
								</div>
								<div class="woo_product_caption center">
									<div class="woo_title">
										<h4 class="woo_pro_title"><a href="{{URL::to('products/'.$category->slug)}}">{{$category->category_name}}</a></h4>
									</div>
								</div>							
							</div>
						</li>
						@endforeach
				
					</ul>
				</div>
				<!-- row -->
						
				<div class="row">
					<div class="col-lg-12">
						<nav aria-label="Page navigation example">
							@if ($getcategory->hasPages())
							<ul class="pagination" role="navigation">
							    {{-- Previous Page Link --}}
							    @if ($getcategory->onFirstPage())
							        <li class="page-item disabled" aria-disabled="true" aria-label="@lang('pagination.previous')">
							            <span class="page-link" aria-hidden="true">&lsaquo;</span>
							        </li>
							    @else
							        <li class="page-item">
							            <a class="page-link" href="{{ $getcategory->previousPageUrl() }}" rel="prev" aria-label="@lang('pagination.previous')">&lsaquo;</a>
							        </li>
							    @endif

							    <?php
							        $start = $getcategory->currentPage() - 2; // show 3 pagination links before current
							        $end = $getcategory->currentPage() + 2; // show 3 pagination links after current
							        if($start < 1) {
							            $start = 1; // reset start to 1
							            $end += 1;
							        } 
							        if($end >= $getcategory->lastPage() ) $end = $getcategory->lastPage(); // reset end to last page
							    ?>

							    @if($start > 1)
							        <li class="page-item">
							            <a class="page-link" href="{{ $getcategory->url(1) }}">{{1}}</a>
							        </li>
							        @if($getcategory->currentPage() != 4)
							            {{-- "Three Dots" Separator --}}
							            <li class="page-item disabled" aria-disabled="true"><span class="page-link">...</span></li>
							        @endif
							    @endif
							        @for ($i = $start; $i <= $end; $i++)
							            <li class="page-item {{ ($getcategory->currentPage() == $i) ? ' active' : '' }}">
							                <a class="page-link" href="{{ $getcategory->url($i) }}">{{$i}}</a>
							            </li>
							        @endfor
							    @if($end < $getcategory->lastPage())
							        @if($getcategory->currentPage() + 3 != $getcategory->lastPage())
							            {{-- "Three Dots" Separator --}}
							            <li class="page-item disabled" aria-disabled="true"><span class="page-link">...</span></li>
							        @endif
							        <li class="page-item">
							            <a class="page-link" href="{{ $getcategory->url($getcategory->lastPage()) }}">{{$getcategory->lastPage()}}</a>
							        </li>
							    @endif

							    {{-- Next Page Link --}}
							    @if ($getcategory->hasMorePages())
							        <li class="page-item">
							            <a class="page-link" href="{{ $getcategory->nextPageUrl() }}" rel="next" aria-label="@lang('pagination.next')">&rsaquo;</a>
							        </li>
							    @else
							        <li class="page-item disabled" aria-disabled="true" aria-label="@lang('pagination.next')">
							            <span class="page-link" aria-hidden="true">&rsaquo;</span>
							        </li>
							    @endif
							</ul>
							@endif
						</nav>
					</div>
				</div>
				
			</div>
			
		</div>
	</div>
</section>
<!-- =========================== Search Products =================================== -->

<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')