/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.language.internal;

import org.gradle.api.model.ObjectFactory;
import org.gradle.language.ComponentWithTargets;
import org.gradle.nativeplatform.OperatingSystemFamily;
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;

public class DefaultTargetMachine implements ComponentWithTargets.TargetMachine {
    private final ObjectFactory objectFactory;

    public DefaultTargetMachine(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public OperatingSystemFamily windows() {
        return objectFactory.named(OperatingSystemFamily.class, OperatingSystemFamily.WINDOWS);
    }

    @Override
    public OperatingSystemFamily linux() {
        return objectFactory.named(OperatingSystemFamily.class, OperatingSystemFamily.LINUX);
    }

    @Override
    public OperatingSystemFamily macOS() {
        return objectFactory.named(OperatingSystemFamily.class, OperatingSystemFamily.MACOS);
    }

    public OperatingSystemFamily host() {
        return objectFactory.named(OperatingSystemFamily.class, DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName());
    }
}
