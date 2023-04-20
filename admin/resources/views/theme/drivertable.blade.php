<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>Profile Image</th>
            <th>Name</th>
            <th>Email</th>
            <th>Mobile</th>
            <th>Created at</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        @foreach ($getdriver as $driver)
        <tr id="dataid{{$driver->id}}">
            <td>{{$driver->id}}</td>
            <td><img src='{!! asset("public/images/profile/".$driver->profile_image) !!}' style="width: 100px;"></td>
            <td>{{$driver->name}}</td>
            <td>{{$driver->email}}</td>
            <td>{{$driver->mobile}}</td>
            <td>{{$driver->created_at}}</td>
            @if (env('Environment') == 'sendbox')
                <td>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$driver->id}}')" title="" data-original-title="Edit">
                        <span class="badge badge-success">Edit</span>
                    </a>
                    @if($driver->is_available == '1')
                        <a class="badge badge-info px-2" onclick="myFunction()" style="color: #fff;">Block</a>
                    @else
                        <a class="badge badge-primary px-2" onclick="myFunction()" style="color: #fff;">Unblock</a>
                    @endif

                    <a data-toggle="tooltip" href="{{URL::to('admin/driver-details/'.$driver->id)}}" data-original-title="View">
                        <span class="badge badge-warning">View</span>
                    </a>
                </td>
            @else
                <td>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$driver->id}}')" title="" data-original-title="Edit">
                        <span class="badge badge-success">Edit</span>
                    </a>
                    @if($driver->is_available == '1')
                        <a class="badge badge-info px-2" onclick="StatusUpdate('{{$driver->id}}','2')" style="color: #fff;">Block</a>
                    @else
                        <a class="badge badge-primary px-2" onclick="StatusUpdate('{{$driver->id}}','1')" style="color: #fff;">Unblock</a>
                    @endif

                    <a data-toggle="tooltip" href="{{URL::to('admin/driver-details/'.$driver->id)}}" data-original-title="View">
                        <span class="badge badge-warning">View</span>
                    </a>
                </td>
            @endif
        </tr>
        @endforeach
    </tbody>
</table>