# Base44 development toolchain for this Android (Kotlin / Jetpack Compose) app.
#
# This image contains ONLY the toolchain (JDK + Android SDK + Gradle). No
# application source is copied in: the repository is bind-mounted at /workspace by
# docker-compose.base44.yml, so every edit is picked up without rebuilding.
FROM eclipse-temurin:21-jdk-jammy

ARG CMDLINE_TOOLS_BUILD=16111833
ARG ANDROID_PLATFORM=android-36.1
ARG BUILD_TOOLS=36.1.0
ARG GRADLE_VERSION=9.3.1

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    GRADLE_USER_HOME=/root/.gradle \
    DEBIAN_FRONTEND=noninteractive \
    PATH=/opt/java/openjdk/bin:/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:/opt/gradle-9.3.1/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      unzip curl ca-certificates git procps \
      libfreetype6 fontconfig fonts-dejavu-core \
 && rm -rf /var/lib/apt/lists/*

# Android command line tools (sdkmanager) + the SDK packages the app compiles against.
RUN mkdir -p "$ANDROID_HOME/cmdline-tools" \
 && curl -fsSL -o /tmp/tools.zip \
      "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_BUILD}_latest.zip" \
 && unzip -q /tmp/tools.zip -d "$ANDROID_HOME/cmdline-tools" \
 && mv "$ANDROID_HOME/cmdline-tools/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest" \
 && rm /tmp/tools.zip

RUN yes | sdkmanager --licenses > /dev/null \
 && sdkmanager --install "platform-tools" "platforms;${ANDROID_PLATFORM}" "build-tools;${BUILD_TOOLS}" > /dev/null

# Gradle, pinned to the version in gradle/wrapper/gradle-wrapper.properties
# (the repository has no gradlew script checked in).
RUN curl -fsSL -o /tmp/gradle.zip \
      "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
 && unzip -q /tmp/gradle.zip -d /opt \
 && rm /tmp/gradle.zip

WORKDIR /workspace
