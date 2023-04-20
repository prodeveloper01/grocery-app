@include('front.theme.header')



<!-- =========================== Breadcrumbs =================================== -->

<div class="breadcrumbs_wrap dark">

	<div class="container">

		<div class="row align-items-center">

			

			<div class="col-lg-12 col-md-12 col-sm-12">

				<div class="text-center">

					<h2 class="breadcrumbs_title">Signup</h2>

					<nav aria-label="breadcrumb">

					  <ol class="breadcrumb">

						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>

						<li class="breadcrumb-item active" aria-current="page">Signup</li>

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



<!-- =========================== Signup =================================== -->

<section>

	<div class="container">

		

		<div class="row">

			<div class="col-md-3"></div>

			<div class="col-lg-6 col-md-12 col-sm-12">

				<div class="login_signup">

					<h3 class="login_sec_title">Signup</h3>

					<form action="{{ URL::to('/signup/signup') }}" method="post">

						@csrf

						<div class="row">

							@if (Session::get('facebook_id') OR Session::get('google_id'))



								<div class="col-lg-12 col-md-12">

									<div class="form-group">

										<label>Full Name</label>

										<input type="text" class="form-control" name="name" placeholder="Full Name" value="{{Session::get('name')}}">

										<span class="error">{{ $errors->login->first('name') }}</span>

									</div>

								</div>

								

								<div class="col-lg-6 col-md-6">

									<div class="form-group">

										<label>Email Address</label>

										<input type="email" class="form-control" name="email" placeholder="Email" value="{{Session::get('email')}}">

										<span class="error">{{ $errors->login->first('email') }}</span>

									</div>

								</div>



								<div class="col-lg-6 col-md-6">

									<div class="form-group">

										<label>Mobile</label>

										<input type="text" class="form-control" name="mobile" placeholder="Mobile" value="{{ old('mobile') }}">

										<span class="error">{{ $errors->login->first('mobile') }}</span>

									</div>

								</div>



								<div class="col-lg-12 col-md-12">

									<div class="form-group">

										<label>Referral code (Optional)</label>

										<input type="text" class="form-control" name="referral_code" placeholder="Referral Code" value="{{ Request()->referral_code }}">

									</div>

								</div>



							@else



							    <div class="col-lg-12 col-md-12">

							    	<div class="form-group">

							    		<label>Full Name</label>

							    		<input type="text" class="form-control" name="name" placeholder="Full Name">

							    		<span class="error">{{ $errors->login->first('name') }}</span>

							    	</div>

							    </div>

							    

							    <div class="col-lg-6 col-md-6">

							    	<div class="form-group">

							    		<label>Email Address</label>

							    		<input type="email" class="form-control" name="email" placeholder="Email">

							    		<span class="error">{{ $errors->login->first('email') }}</span>

							    	</div>

							    </div>



							    <div class="col-lg-6 col-md-6">

							    	<div class="form-group">

							    		<label>Mobile</label>

							    		<input type="text" class="form-control" name="mobile" placeholder="Mobile">

							    		<span class="error">{{ $errors->login->first('mobile') }}</span>

							    	</div>

							    </div>

							    

							    <div class="col-lg-6 col-md-6">

							    	<div class="form-group">

							    		<label>Password</label>

							    		<input type="password" class="form-control" name="password" placeholder="Password">

							    		<span class="error">{{ $errors->login->first('password') }}</span>

							    	</div>

							    </div>

							    

							    <div class="col-lg-6 col-md-6">

							    	<div class="form-group">

							    		<label>Confirm Password</label>

							    		<input type="password" class="form-control" name="password_confirmation" placeholder="Confirm Password">

							    	</div>

							    </div>



							    <div class="col-lg-12 col-md-12">

							    	<div class="form-group">

							    		<label>Referral code (Optional)</label>

							    		<input type="text" class="form-control" name="referral_code" placeholder="Referral code" value="{{ Request()->referral_code }}">

							    	</div>

							    </div>



							@endif



							<div class="col-lg-12 col-md-12">

								<div class="login_flex">

									<div class="login_flex_1">

										<input name="accept" id="accept" class="checkbox-custom" type="checkbox">

										<label for="accept" class="checkbox-custom-label">I accept the <a href="{{URL::to('/terms')}}"> terms & conditions</a></label>

									</div>

									<div class="login_flex_2">

										Already have an <a href="{{URL::to('/signin')}}" class="text-bold"> Account?</a>

									</div>

								</div>

								<span class="error">{{ $errors->login->first('accept') }}</span>

								<div class="form-group">

									<button type="submit" class="btn btn-md btn-theme col-md-12 mt-3">Signup</button>

								</div>

							</div>



						</div>

			

					

					</form>

				</div>

			</div>

			

		</div>

	</div>

</section>

<!-- =========================== Signup =================================== -->



<!-- ======================== Fresh Vegetables & Fruits End ==================== -->

@include('front.theme.footer')