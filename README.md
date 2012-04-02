# Android WebImage Library

This library allows developers to easily integrate images from the web in an Android application.

## Features

- ImageView class
- Drawable class (useful for map items)
- File cache (on the SDCard, lifetime is adjustable)
- Memory cache (LRU cache, size is adjustable)

## Sample

A sample application is available: [WebImage Sample](https://play.google.com/store/apps/details?id=org.pierrre.webimage.sample).

![Phone landscape](https://github.com/pierrre/Android-WebImage/raw/master/sample/project/phone-landscape.png)
![Phone portrait list](https://github.com/pierrre/Android-WebImage/raw/master/sample/project/phone-portrait-list.png)
![Phone portrait flag](https://github.com/pierrre/Android-WebImage/raw/master/sample/project/phone-portrait-flag.png)
![Tablet](https://github.com/pierrre/Android-WebImage/raw/master/sample/project/tablet.png)

The source code is available in this repository.

## Compatibility

This library is compatible from API 7 (Android 2.1).

## Installation

The library project requires:

- [Android-ThreadPoolExecutorAsyncTask](https://github.com/pierrre/Android-ThreadPoolExecutorAsyncTask)

The sample project requires:

- The library project
- [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
- [Android-AdapterItem](https://github.com/pierrre/Android-AdapterItem)

## Usage

Add the View to the layout:

``` xml
<org.pierrre.webimage.WebImageView
    android:id="@+id/image"
    android:layout_width="100dp"
    android:layout_height="100dp" />
```

Set the image url:

``` java
WebImageView image = (WebImageView) findViewById(R.id.image);
image.setImageUrl("http://www.example.com/image.png");
```

## Contact

- Email: pierredurand@gmail.com
- Twitter: [@pierredurand87](https://twitter.com/pierredurand87)
- Google+: [Pierre Duraand](https://plus.google.com/115978530878583279430)
- GitHub: [pierrre](https://github.com/pierrre)