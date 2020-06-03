# Библиотека LimeHD-Ads-Sdk

![Release](https://img.shields.io/github/v/release/LimeHD/limehd-ads-sdk)

LimeHD-Ads-Sdk помагает разработчикам интегрировать рекламу в их приложения. Библиотека поддерживает несколько рекламных площадок IMA SDK, Yandex, Google, myTarget

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
## Примеры использования
### 0. Инициализация библиотеки `LimeAds`
Для использования библиотеки необходимо создать экземпляр
``` kotlin
LimeAds limeAds = new LimeAds(context, new JSONobject())
```
### 1. Полчить рекламу myTarget
- **context**: Контекст приложения
- **resId**: Место, куда приложение хочет вставить фрагмент с рекламой ***(R.id.main_container)***
- **fragmentStateCallback**: Callback с положительным и отрицательным результатом
``` js
limeAds.getMyTargetAd(context, resId, fragmentStateCallback);
```
### 2. Добавить FragmentState
Callback, который является одним из параметров в функции 
``` js
limeAds.getMyTargetAd(context, resId, fragmentStateCallback);
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

