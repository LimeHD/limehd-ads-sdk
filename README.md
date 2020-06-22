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
- Для использования библиотеки необходимо создать экземпляр и обернуть метод init() в блок try | catch. Так как при неверных введённых параметрах будет выбрасываться IllegalArgumentException
- Инициализировав библиотеку `LimeAds.init(...)` приложение может использовать её по всему проекту, тем самым не создавать еще одни экземпляры
- Для большей информации о загрузки и показа рекламы в метод `LimeAds.getAd(..., adRequestCallback, adShowCallback)` можно добавить листенеры. По умолчанию их значение **null**
  - *import tv.limehd.adsmodule.interfaces.AdRequestListener*
  - *import tv.limehd.adsmodule.interfaces.AdShowListener*
``` kotlin
try {
    LimeAds.init(new JSONObject(Constants.json));
    LimeAds.getAd(context, R.id.main_container, fragmentStateCallback, null, null);
}catch (IllegalArgumentException | JsonException error) {
    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show();
}
```
### 1. Получить рекламу от библиотеки
- **context**: Context Activity, Fragment. На котором иниц-ется библиотека
- **R.id.main_container**: Место, куда приложение хочет вставить фрагмент с рекламой
- **fragmentStateCallback**: Callback с положительным и отрицательным результатом
- @Nullable **adRequestCallback**: Листенер для логов запроса рекламы
- @Nullable **adShowCallback**: Листенер для логов показа рекламы
``` js
LimeAds.getAd(context, R.id.main_container, fragmentStateCallback, adRequestCallback, adShowCallback);
```
### 2. Добавить FragmentState
Callback, который является одним из параметров в функции 
``` js
LimeAds.getAd(context, R.id.main_container, fragmentStateCallback, adRequestCallback, adShowCallback);
```
``` kotlin
private FragmentState fragmentStateCallback = new FragmentState() {
    @Override
    public void onSuccessState(@NotNull Fragment fragment) {
        // положительный ответ. fragment с рекламой
        // Чтобы показать рекламу. Просто вызовите функцию
        LimeAds.showAd(fragment);
    }

    @Override
    public void onErrorState(@NotNull String message) {
        // отрицательный ответ. message - сообщение с ошибкой
    }
};
```

