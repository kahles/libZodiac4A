# libZodiac
framework providing functionality for building a lunar calendar

### Motivation
While writing [Mondtag](https://github.com/kahles/mondtag), I discovered that testing and experimenting with this code sucks in AndroidStudio with Android emulator - so I decided to outsource this into an external library ...

### Requirements

#### Java 8
I decided to use JDK8, even though it isn't fully supported by Android yet. E.g. `java.time` is only supported through a backport for Android.

#### gradle
A [gradle](https://gradle.org) build script is included to manage dependencies and automate testing and building. The plugin [gradle-android-git-version](https://github.com/gladed/gradle-android-git-version) is used to automate versioning based on git and also works well in a non-Android java project.

#### libnova
Astronomical calculations are done with the help of [libnova](http://libnova.sourceforge.net/) by Liam Girdwood. Because the library is written in C, the
portation [novaforjava v0.15.0.0](http://novaforjava.sourceforge.net/) by Richard van Nieuwenhoven is used.

#### JUnit 4.12
I'm trying to provide a reasonable test coverage using [JUnit](http://junit.org).

#### slf4j
For logging I decided to use [slf4j](http://www.slf4j.org/), because it has many backends including one for Android logging.

### Usage
- [`CalendarExampleSimple.java`](src/test/java/de/kah2/libZodiac/example/CalendarExampleSimple.java) shows basic usage of this framework.
- [`CalendarDataStringBuilder.java`](src/test/java/de/kah2/libZodiac/example/CalendarDataStringBuilder.java) shows how to access data. 
- [`CalendarExampleStorage.java`](src/test/java/de/kah2/libZodiac/example/CalendarExampleStorage.java) shows how to create, store , load and extend a calendar.
- [`ProgressListenerExample.java`](src/test/java/de/kah2/libZodiac/example/ProgressListenerExample.java) shows how to enable progress listening.

### Further references
- Book: [Vom richtigen Zeitpunkt](http://www.paungger-poppe.com/index.php/de/publikationen/unsere-buecher/vom-richtigen-zeitpunkt) This book also is available in other languages, the English version is called "The Power of Timing".

### License
This project is licensed under the GNU General Public License v3. See [LICENSE](LICENSE) for details.

### Contact
Feel free to contact me if you have wishes, proposals or if want to contribute.
