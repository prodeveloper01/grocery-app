@include('front.theme.header')



<!-- =========================== Breadcrumbs =================================== -->

<div class="breadcrumbs_wrap dark">

	<div class="container">

		<div class="row align-items-center">

			

			<div class="col-lg-12 col-md-12 col-sm-12">

				<div class="text-center">

					<h2 class="breadcrumbs_title">About</h2>

					<nav aria-label="breadcrumb">

					  <ol class="breadcrumb">

						<li class="breadcrumb-item"><a href="#"><i class="ti-home"></i></a></li>

						<li class="breadcrumb-item active" aria-current="page">About</li>

					  </ol>

					</nav>

				</div>

			</div>

			

		</div>

	</div>

</div>

<!-- =========================== Breadcrumbs =================================== -->



<!-- =========================== About Us =================================== -->

<section>

	<div class="container">

		<div class="row">

		

			<div class="col-lg-12 col-md-6 col-sm-12">

				<p>{!!$getabout->about_content!!}</p>

			</div>

			

		</div>

	</div>

</section>

<!-- =========================== About Us =================================== -->

<!-- ======================== Fresh Vegetables & Fruits End ==================== -->

@include('front.theme.footer')