from django.http import HttpResponse, JsonResponse

import json


def custom_middleware(org_f):
    def new_func(request,*original_args, **original_kwargs):
        if request.content_type == 'application/json':
            try:
                    request.json = json.loads(str(request.body.decode('utf-8')).strip('\r\n\t'))
            except:
                    return JsonResponse({"message": "Invalid json data"}, status=401)

            return org_f(request, *original_args, **original_kwargs)

        else:
            return org_f(request,*original_args, **original_kwargs)
    return new_func