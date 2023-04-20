@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
	<div class="container">
		<div class="row align-items-center">
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="text-center">
					<h2 class="breadcrumbs_title">Account Details</h2>
					<nav aria-label="breadcrumb">
					  <ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">Account Details</li>
					  </ol>
					</nav>
				</div>
			</div>
			
		</div>
	</div>
</div>
<!-- =========================== Breadcrumbs =================================== -->

<!-- =========================== Account Settings =================================== -->
<section class="gray">
	<div class="container">
		@if(session()->has('message'))
		    <div class="alert alert-success">
		        {{ session()->get('message') }}
		    </div>
		@endif
		@if(session()->has('danger'))
		    <div class="alert alert-danger">
		        {{ session()->get('danger') }}
		    </div>
		@endif
		<div class="row">
			@include('front.theme.sidebar')
			
			<div class="col-lg-8 col-md-9">
				<!-- Total Items -->
				<div class="card style-2 mb-4">
					<form class="submit-page" action="{{ URL::to('/account/updateaccount') }}" method="post">
						<div class="card-header">
							<h4 class="mb-0">Account Details</h4>
						</div>
						<div class="card-body">
						
							@csrf
							<div class="row">
							
								<div class="col-12">
								  <!-- Email -->
								  <div class="form-group">
									<label>Full Name *</label>
									<input class="form-control" type="text" placeholder="First Name *" value="{{Auth::user()->name}}" name="name" required="">
								  </div>

								</div>
							
								<div class="col-12">
									<!-- Email -->
									<div class="form-group">
										<label> Email Address *</label>
										<input class="form-control" type="email" placeholder="Email Address *" value="{{Auth::user()->email}}" name="email" readonly="">
									</div>
								</div>

								<div class="col-12">
									<!-- Email -->
									<div class="form-group">
										<label> Mobile Number *</label>
										<input class="form-control" type="text" placeholder="Mobile Number *" value="{{Auth::user()->mobile}}" name="mobile" readonly="">
									</div>
								</div>
							</div>
						</div>

						@if (Auth::user()->login_type == "email")
						<div class="card-header">
							<h4 class="mb-0">Change Password</h4>
						</div>
						<div class="card-body">
							<div class="row">
								<div class="col-12">
									<!-- Password -->
									<div class="form-group">
										<label>Current Password *</label>
										<input class="form-control" type="password" name="oldpassword" placeholder="Current Password *">
										<span class="error">{{ $errors->account->first('oldpassword') }}</span>
									</div>
								</div>
							
								<div class="col-12 col-md-6">
									<!-- Password -->
									<div class="form-group">
										<label>New Password *</label>
										<input class="form-control" name="newpassword" type="password" placeholder="New Password *">
										<span class="error">{{ $errors->account->first('newpassword') }}</span>
									</div>
								</div>

								<div class="col-12 col-md-6">
									<!-- Password -->
									<div class="form-group">
										<label>Confirm Password *</label>
										<input class="form-control" name="confirmpassword" type="password" placeholder="Confirm Password *">
										<span class="error">{{ $errors->account->first('confirmpassword') }}</span>
									</div>
								</div>
							</div>						
						</div>
						@endif
						<div class="col-12 card-body">
						  <button class="btn btn-dark" type="submit">Save Changes</button>
						</div>
					</form>
				</div>
			</div>
			
		</div>
	</div>
</section>
<!-- =========================== Account Settings =================================== -->
<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')