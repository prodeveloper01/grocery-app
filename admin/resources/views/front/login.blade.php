@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">Signin</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">Signin</li>
					  </ol>
					</nav>
				</div>
			</div>
			
		</div>
	</div>
</div>
<!-- =========================== Breadcrumbs =================================== -->

@if (\Session::has('danger'))
    <div class="alert alert-danger text-center">
        {!! \Session::get('danger') !!}
    </div>
@endif

@if (\Session::has('success'))
    <div class="alert alert-success text-center">
        {!! \Session::get('success') !!}
    </div>
@endif

<!-- =========================== Login/Signup =================================== -->
<section>
	<div class="container">
		
		<div class="row">
			<div class="col-md-3"></div>
			<div class="col-lg-6 col-md-12 col-sm-12">
				<div class="login_signup">
					<h3 class="login_sec_title">Signin</h3>
					<form id="login" action="{{ URL::to('/signin/login') }}" method="post">
            			@csrf
						<div class="form-group">
							<label>Email Address</label>
							<input type="text" class="form-control" name="email" id="email" placeholder="Email">
							<span class="error">{{ $errors->login->first('email') }}</span>
						</div>
						
						<div class="form-group">
							<label>Password</label>
							<input type="password" class="form-control" name="password" id="password" placeholder="Password">
							<span class="error">{{ $errors->login->first('password') }}</span>
						</div>
						
						<div class="login_flex">
							<div class="login_flex_1">
								New User? <a href="{{URL::to('/signup')}}" class="text-bold">Create Account</a>
							</div>
							<div class="login_flex_2">
								<a href="{{URL::to('/forgot-password')}}" class="text-bold">Forgot Password?</a>
								<!-- <div class="form-group mb-0">
									<button type="submit" class="btn btn-md btn-theme">Login</button>
								</div> -->
							</div>
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-md btn-theme col-md-12 mt-3">Login</button>
							<div class="text-center">
							<a href="{{ url('auth/google') }}" class="btn btn-primary col-md-5 mt-3 mr-3" style="background-color: #fff;">
							    <img src='{!! asset("public/assets/images/ic_google.png") !!}' alt="">
							</a>
							<a href="{{ url('auth/facebook') }}" class="btn btn-primary col-md-5 mt-3" style="background-color: #fff;">
							    <img src='{!! asset("public/assets/images/ic_fb.png") !!}' alt="">
							</a>
							</div>
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