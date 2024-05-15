val state = rememberCropifyState()

Cropify(
uri = imageUri,
state = state,
onImageCropped = {},
onFailedToLoadImage = {}
)

val state = rememberCropifyState()

Cropify(
bitmap = imageResource(R.drawable.bitmap),
state = state,
onImageCropped = {},
)

