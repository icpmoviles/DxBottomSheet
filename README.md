# Dx Bottom Sheet

Libreria de soporte para crear y mostrar de forma sencilla BottomSheetDialogFragment*.

*Esta es una versión DialogFragment que muestra una hoja inferior en lugar de un cuadro de diálogo flotante.
## Prerequisites

Add this in your root build.gradle file (not your module build.gradle file):

```kotlin
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
## Dependency

Add this to your module's build.gradle file (make sure the version matches the JitPack badge above):

```kotlin
dependencies {
    ...
    implementation 'com.github.icpmoviles:DxBottomSheet:VERSION'
}
```

# Documentation

## Funciones del builder

Añadir el icono a mostrar (todos)
    
    fun setIcon(@DrawableRes icon: Int)

Añadir el titulo del dialogo (todos los tipos de dx)
    
    fun setTitle(title: String)

Añadir el mensaje del dialogo (Info() y Action())

    fun setMessage(message: String)

Cambiar el tema predefinido (todos) 
*(mas informacion en estilos)*

    fun setTheme(@StyleRes theme: Int)
    

Cambiar la cancelacion del dialogo (todos) 

    fun setCancelable(cancelable: Boolean = true)

Cambiar la cancelacion al tocar fuera del dialogo (todos)

    fun setCancelOnTouchOutSide(cancelOnTouchOutSide: Boolean = true)

Añadir el archivo de recursos del lottie (Lottie())

    fun setLottie(@RawRes lottieRaw: Int)

Indica si queremos repetir en bucle la animacion (Lottie())

    fun setLottieLoop(lottieLoop: Boolean = true)


Indica si queremos controlar la accion de replegar el dialogo manualmente (Action())

    fun setControlDismiss(controlDismiss: Boolean = false)

Funcion de finalizacion que crea y devuelve sin mostrar un objeto BottomSheetDx (todos) 

    fun build()

Funcion de finalizacion que crea y muestra un objeto BottomSheetDx (todos) 

    fun buildAndShow(fragmentManager: FragmentManager)

### Listeners

Para que los botones sean mostrados hay que tener alguno de los dos tipos de botones definidos. Si deseas que el dialogo permanezca visible mientras se ejecuta tu accion debes activar el controlDismiss.
Mostrar un boton que solo haga la funcion de cancelar no tiene sentido aqui ya que esa accion la provoca el deslizamiento hacia abajo.
Mostrar los dos botones esta pensado para momentos que se deban realizar acciones ejemplos:
"guardar y salir" y "salir sin guardar"

Muestra el boton con estilo positivo (Action())

    fun setPositiveButton(textButton: String, onClickListener: ((BottomSheetDx) -> Unit)?)

Muestra el boton con estilo negativo *(style = bordeless)* (Action())

    fun setNegativeButton(textButton: String, onClickListener: ((BottomSheetDx) -> Unit)?)


## Funciones del dialogo
Aparte de las propias de la clase BottomSheetDialogFragment 

Mostrar un objeto BottomSheetDx

    fun show(fragmentManager: FragmentManager)

Modifica el estado del dialogo al estado inicial. Pensado para casos donde queremos mantener el dialogo y ejecutar una accion, en caso de recibir un error hay que reiniciar el estado ya que los botones se autodesactivan al invocar al listener. *Solo tiene sentido en caso de tener setControlDismiss = true. En caso contrario no es necesario controlar esto*

    fun setInitialUiState()


## Style

Para añadir un tema nuevo a los dialogos hay que crear un nuevo estilo dentro de el archivo themes.xml de la siguiente manera:

    
    <style name="MiCustomTheme" parent="BottomSheetDxBaseTheme">
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowIsFloating">false</item>
        <item name="colorPrimary">@color/primaryColorDx</item>
        <item name="colorSecondary">@color/primaryColorDx</item>
        <item name="android:windowBlurBehindEnabled" tools:targetApi="s">true</item>
        <item name="android:windowBlurBehindRadius" tools:targetApi="s">4dp</item>
    </style>


Para indicar el color primario al dx, el cual sera usado para los iconos y botones, se debe crear dentro de colors.xml

        <color name="primaryColorDx">TUCOLORPRIMARIO</color>
## Usage/Examples

Crear Dialogo de información
```kotlin
    val dialogo = BottomSheetDx.Builder.Info()
        .setIcon(icon)
        .setTitle(title)
        .setMessage(message)
        .setTheme(theme)
        .setCancelable(cancelable)
        .setCancelOnTouchOutSide(cancelOnTouchOutSide)
        .build()
        
    dialogo.show(FragmentManager)
```

Dialogo con Lottie
```kotlin
    val dialogo = BottomSheetDx.Builder.Lottie()
            .setIcon(icon)
            .setTitle(title)
            .setTheme(theme)
            .setCancelable(cancelable)
            .setCancelOnTouchOutSide(cancelOnTouchOutSide)
            .setLottie(lottieRaw)
            .setLottieLoop(lottieLoop)
            .build()
    dialogo.show(FragmentManager)
        
```

Dialogo de Acción
```kotlin
    BottomSheetDx.Builder.Action()
        .setIcon(icon)
        .setTitle(title)
        .setMessage(message)
        .setTheme(theme)
        .setCancelable(cancelable)
        .setCancelOnTouchOutSide(cancelOnTouchOutSide)
        .setControlDismiss(controlDismiss)
        .setPositiveButton(textButton) { dx ->
            // TODO add your action
        }
        .setNegativeButton(textButton) { dx ->
            // TODO add your action
        }
        .buildAndShow(FragmentManager)
```


## Authors

- [@Carlos del Campo](https://www.github.com/icpmoviles)

