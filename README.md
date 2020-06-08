# Библиотека LimeHD-Ads-Sdk

![Release](https://img.shields.io/github/v/release/LimeHD/limehd-ads-sdk)

LimeHD-Ads-Sdk помогает разработчикам интегрировать рекламу в их приложения. Библиотека поддерживает несколько рекламных площадок IMA SDK, Yandex, Google, myTarget

## Интеграция

### 0. Добавить `JitPack` в build.gradle файл
``` gradle
allprojects {
  repositories {
    ....
    maven { url 'https://jitpack.io' }
  }
}
```

### 1. Добавить dependency build.gradle
``` gradle
dependencies {
  implementation 'com.github.LimeHD:limehd-ads-sdk:X.X.X'
}
```

### 2. Добавить compileOptions в build.gradle
``` gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

### 3. Добавить uses-permission в AndroidManifest
``` xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

## Примеры использования
### 0. Инициализация библиотеки `LimeAds`
Для использования библиотеки необходимо создать экземпляр
``` kotlin
LimeAds limeAds = new LimeAds(context, new JSONobject())
```
### 1. Получить рекламу от библиотеки
- **R.id.main_container**: Место, куда приложение хочет вставить фрагмент с рекламой
- **fragmentStateCallback**: Callback с положительным и отрицательным результатом
- **main_container**: ViewGroup, куда приложение хочет вставить фрагмент с рекламой
``` js
limeAds.getAd(R.id.main_container, fragmentStateCallback, main_container);
```
### 2. Добавить FragmentState
Callback, который является одним из параметров в функции 
``` js
limeAds.getAd(R.id.main_container, fragmentStateCallback, main_container);
```
``` kotlin
private FragmentState fragmentStateCallback = new FragmentState() {
    @Override
    public void onSuccessState(@NotNull Fragment fragment) {
        // положительный ответ. fragment с рекламой
    }

    @Override
    public void onErrorState(@NotNull String message) {
        // отрицательный ответ. message - сообщение с ошибкой
    }
};
```

