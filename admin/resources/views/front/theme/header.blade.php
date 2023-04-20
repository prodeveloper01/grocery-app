<!DOCTYPE html>
<!-- <html lang="en" dir="rtl"> -->
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<meta name="author" content="www.frebsite.nl" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<meta name="csrf-token" id="csrf-token" content="{{ csrf_token() }}">
		<link rel="icon" href='{!! asset("public/images/about/".$getabout->favicon) !!}' type="image/gif" sizes="16x16">
		
        <title>{{$getabout->title}}</title>
		 
        <!-- Custom CSS -->
        <link href="{!! asset('public/front/css/styles.css') !!}" rel="stylesheet">
        <link href="https://res.cloudinary.com/dxfq3iotg/raw/upload/v1557232134/toasty.css" rel="stylesheet" />
        <link href="{!! asset('public/assets/plugins/sweetalert/css/sweetalert.css') !!}" rel="stylesheet">
		
    </head>
	
    <body class="grocery-theme">
	
        <!-- ============================================================== -->
        <!-- Preloader - style you can find in spinners.css -->
        <!-- ============================================================== -->
        <div id="preloader"><div class="preloader"><span></span><span></span></div></div>
		
		
        <!-- ============================================================== -->
        <!-- Main wrapper - style you can find in pages.scss -->
        <!-- ============================================================== -->
        <div id="main-wrapper">
		
            <!-- ============================================================== -->
            <!-- Top header  -->
            <!-- ============================================================== -->
            <!-- Start Navigation -->
			<div class="header">
				<!-- Main header -->
				<div class="main_header">
					<div class="container">
						<div class="row align-items-center">
							<div class="col-lg-2 col-md-2 col-sm-3 col-4">
								<a class="nav-brand" href="{{URL::to('/')}}">
									<img src='{!! asset("public/images/about/".$getabout->logo) !!}' class="logo" alt="" style="height: 50px;" />
								</a>
							</div>
							<div class="col-lg-10 col-md-10 col-sm-9 col-8">
								<!-- Show on Mobile & iPad -->
								<div class="blocks shop_cart d-xl-none d-lg-none">
									<div class="single_shop_cart">
										<div class="ss_cart_left">
											<a class="cart_box" data-toggle="collapse" href="#mySearch" role="button" aria-expanded="false" aria-controls="mySearch"><i class="ti-search"></i></a>
										</div>
									</div>
									<div class="single_shop_cart">
										<div class="ss_cart_left">
											@if (@Auth::user()->id != "" && @Auth::user()->type != "1")
												@if (!request()->is('cart'))
													<a href="#" onclick="openRightMenu()" id="openRightMenu" class="cart_box">
												@else
													<a href="#" class="cart_box">
												@endif
													<span class="qut_counter">{{Storage::disk('local')->get("cart")}}</span><i class="ti-shopping-cart"></i>
												</a>
											@else
												<a href="#"  onclick="openRightMenu()" id="openRightMenu" class="cart_box"><span class="qut_counter">0</span><i class="ti-shopping-cart"></i></a>
											@endif
										</div>
										<div class="ss_cart_content">
											<strong>My Cart</strong>
										</div>
									</div>
								</div>
								
								<!-- Show on Desktop -->
								<div class="blocks shop_cart d-none d-xl-block d-lg-block">
									<div class="single_shop_cart">
										<div class="ss_cart_left">
											<a href="javascript:void(0)" class="cart_box"><i class="lni lni-phone"></i></a>
										</div>
										<div class="ss_cart_content">
											<strong>Call Us:</strong>
											<span>{{$getabout->mobile}}</span>
										</div>
									</div>
									<div class="single_shop_cart">
										<div class="ss_cart_left">
											@if (!request()->is('cart'))
												<a href="#" onclick="openRightMenu()" id="openRightMenu" class="cart_box">
											@else
												<a href="#" class="cart_box">
											@endif
												@if (@Auth::user()->id != "" && @Auth::user()->type != "1")
													<span class="qut_counter">{{Storage::disk('local')->get("cart")}}</span><i class="ti-shopping-cart"></i>
												@else
													<span class="qut_counter">0</span><i class="ti-shopping-cart"></i>
												@endif
											</a>
										</div>
										<div class="ss_cart_content">
											<strong>My Cart</strong>
										</div>
									</div>
								</div>
								
								<div class="blocks search_blocks d-none d-xl-block d-lg-block">
									<form method="get" action="{{URL::to('/search')}}">
										<div class="input-group">
											<input type="text" name="item" class="form-control" placeholder="Search entire store here...">
											<div class="input-group-append">
												<button class="btn search_btn" type="submit"><i class="ti-search"></i></button>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
					<div class="collapse" id="mySearch">
						<div class="blocks search_blocks">
							<form method="get" action="{{URL::to('/search')}}">
								<div class="input-group">
									<input type="text" name="item" class="form-control" placeholder="Search entire store here...">
									<div class="input-group-append">
									<button class="btn search_btn" type="submit"><i class="ti-search"></i></button>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
				
				<div class="header_nav">
					<div class="container">	
						<div class="row align-item-center">
							<div class="col-lg-3 col-md-4 col-sm-8 col-10">
								<!-- For Desktop -->
								<div class="shopby_categories d-none d-xl-block d-lg-block">
									<a class="shop_category" data-toggle="collapse" href="#myCategories" role="button" aria-expanded="false" aria-controls="myCategories"><i class="ti-menu"></i>Shop by categories</a>
									<div class="collapse" id="myCategories">
										<div id="cats_menu">
											<ul>
												<?php $count = 0; ?>
												@foreach ($getcategory as $category)
												<?php if($count == 6) break; ?>
													<li><a href="{{URL::to('products/'.$category->slug)}}"><span>{{$category->category_name}}</span></a></li>
												<?php $count++; ?>
												@endforeach
												<li><a href="{{URL::to('/category')}}"><span>View All <i class="fa fa-arrow-right" aria-hidden="true"></i></span></a></li>
											</ul>
										</div>
									</div>
								</div>
								
								<!-- For Mobile -->
								<div class="shopby_categories d-xl-none d-lg-none">
									<a class="shop_category" href="#" onclick="openLeftMenu()"><i class="ti-menu"></i>Shop By categories</a>
								</div>
								
							</div>
							
							<div class="col-lg-9 col-md-8 col-sm-4 col-2">
								<nav id="navigation" class="navigation navigation-landscape">
									<div class="nav-header">
										<div class="nav-toggle"></div>
									</div>
									<div class="nav-menus-wrapper" style="transition-property: none;float: right;">
										<ul class="nav-menu">
										
											<li class="{{ request()->is('/') ? 'active' : '' }}"><a href="{{URL::to('/')}}">Home</a></li>

											<li class="{{ request()->is('latest-products') ? 'active' : '' }}"><a href="{{URL::to('/latest-products')}}">Latest Products</a></li>

											<li class="{{ request()->is('explore-products') ? 'active' : '' }}"><a href="{{URL::to('/explore-products')}}">Explore Products</a></li>

											@if (@Auth::user()->id != "" && @Auth::user()->type != "1")
												<li class="nav-item dropdown {{ request()->is('wallet') ? 'active' : '' }}{{ request()->is('favorite') ? 'active' : '' }}{{ request()->is('orders') ? 'active' : '' }}">
													<a class="nav-link dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" href="javascript:void(0)">
														Hello, {{Auth::user()->name}}
													</a>
													<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
														<a class="dropdown-item" href="{{URL::to('/address')}}">My Address</a>
														<a class="dropdown-item" href="{{URL::to('/account')}}">My Account</a>
														<a class="dropdown-item" href="{{URL::to('/wallet')}}">My Wallet</a>
														<a class="dropdown-item" href="" data-toggle="modal" data-target="#AddReview">Add Review</a>
														<a class="dropdown-item" href="" data-toggle="modal" data-target="#Refer">Refer and earn</a>
														<a class="dropdown-item" href="{{URL::to('/logout')}}">Logout</a>
													</div>
												</li>
											@else 
												<li class="{{ request()->is('/signin') ? 'active' : '' }}"><a href="{{URL::to('/signin')}}">Login</a></li>
											@endif
											
										</ul>

									</div>
								</nav>
							</div>
						</div>
					</div>
					
				</div>
				
			</div>
			<!-- End Navigation -->
			<div class="clearfix"></div>
			<!-- ============================================================== -->
			<!-- Top header  -->
			<!-- ============================================================== -->