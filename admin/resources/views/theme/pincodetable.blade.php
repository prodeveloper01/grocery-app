<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>Pincode</th>
            <th>Created at</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <?php
        foreach ($getpincode as $pincode) {
        ?>
        <tr id="dataid{{$pincode->id}}">
            <td>{{$pincode->id}}</td>
            <td>{{$pincode->pincode}}</td>
            <td>{{$pincode->created_at}}</td>
            @if (env('Environment') == 'sendbox')
                <td>
                    @if ($pincode->is_available == 1)
                        <a class="badge badge-success px-2" onclick="myFunction()" style="color: #fff;">Available</a>
                    @else
                        <a class="badge badge-danger px-2" onclick="myFunction()" style="color: #fff;">Unavailable</a>
                    @endif
                </td>
                <td>
                    <span>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$pincode->id}}')" title="" data-original-title="Edit">
                            <span class="badge badge-success">Edit</span>
                        </a>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="myFunction()" title="" data-original-title="Delete">
                            <span class="badge badge-danger">Delete</span>
                        </a>
                    </span>
                </td>
            @else
                <td>
                    @if ($pincode->is_available == 1)
                        <a class="badge badge-success px-2" onclick="StatusUpdate('{{$pincode->id}}','2')" style="color: #fff;">Available</a>
                    @else
                        <a class="badge badge-danger px-2" onclick="StatusUpdate('{{$pincode->id}}','1')" style="color: #fff;">Unavailable</a>
                    @endif
                </td>
                <td>
                    <span>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$pincode->id}}')" title="" data-original-title="Edit">
                            <span class="badge badge-success">Edit</span>
                        </a>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="Delete('{{$pincode->id}}')" title="" data-original-title="Delete">
                            <span class="badge badge-danger">Delete</span>
                        </a>
                    </span>
                </td>
            @endif
        </tr>
        <?php
        }
        ?>
    </tbody>
</table>