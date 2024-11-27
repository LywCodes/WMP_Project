import requests
from io import BytesIO

def detect_food_bytes(image_bytes, api_user_token):
    headers = {'Authorization': 'Bearer ' + api_user_token}
    url = 'https://api.logmeal.com/v2/image/segmentation/complete'
    files = {'image': ('image.jpg', BytesIO(image_bytes), 'image/jpeg')}
    resp = requests.post(url, files=files, headers=headers)

    url = 'https://api.logmeal.com/v2/recipe/ingredients'
    resp = requests.post(url,json={'imageId': resp.json()['imageId']}, headers=headers)
    return resp.json()["foodName"]  # Return the JSON response
