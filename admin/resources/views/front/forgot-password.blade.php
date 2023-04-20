@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">Forgot Password</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">Forgot Password</li>
					  </ol>
					</nav>
				</div>
			</div>
			
		</div>
	</div>
</div>
<!-- =========================== Breadcrumbs =================================== -->

<!-- =========================== Login/Signup =================================== -->
<section>
	<div class="container">
		
		<div class="row">
			<div class="col-md-3"></div>
			<div class="col-lg-6 col-md-12 col-sm-12">
				<div class="login_signup">
					<h3 class="login_sec_title">Forgot Password</h3>
					<form id="login" action="{{ URL::to('/forgot-password/forgot-password') }}" method="post">
            			@csrf
						<div class="form-group">
							<label>Email Address</label>
							<input type="text" class="form-control" name="email" id="email" placeholder="Email">
							<span class="error">{{ $errors->login->first('email') }}</span>
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-md btn-theme col-md-12 mt-3">Submit</button>
						</div>
						
					</form>
				</div>
			</div>
			
		</div>
	</div>
</section>
<!-- =========================== Login/Signup =================================== -->

<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')