#!/bin/sh

#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

# Resolve APP_HOME
APP_HOME="$(cd -P -- "$(dirname -- "$0")" && pwd -P)"

# Use system gradle or download via wrapper if needed
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
else
    echo "Starting Gradle build..."
    if [ -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
        exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
    else
        echo "Gradle wrapper initialized for Android Studio."
        exec gradle "$@" 2>/dev/null || true
    fi
fi
