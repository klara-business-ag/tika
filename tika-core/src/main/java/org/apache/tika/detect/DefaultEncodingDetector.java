/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.tika.detect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.ServiceRegistry;
import org.apache.tika.config.ServiceLoader;

/**
 * A composite encoding detector based on all the {@link EncodingDetector} implementations
 * available through the {@link ServiceRegistry service provider mechanism}.  Those
 * loaded via the service provider mechanism are ordered by how they appear in the
 * file, if there is a single service file.  If multiple, there is no guarantee of order.
 *
 *
 * If you need to control the order of the Detectors, you should instead
 *  construct your own {@link CompositeDetector} and pass in the list
 *  of Detectors in the required order.
 *
 * @since Apache Tika 1.15
 */
public class DefaultEncodingDetector extends CompositeEncodingDetector {

    private static final long serialVersionUID = 1L;

    private ServiceLoader loader;

    private List<EncodingDetector> dynamicDetectors;
    private boolean loadDynamicServices;

    public DefaultEncodingDetector() {
        this(false);
    }

    public DefaultEncodingDetector(boolean loadDynamicServices) {
        this(new ServiceLoader(DefaultEncodingDetector.class.getClassLoader(), loadDynamicServices));
        this.loadDynamicServices = loadDynamicServices;
    }

    public DefaultEncodingDetector(ServiceLoader loader) {
        super(loader.loadStaticServiceProviders(EncodingDetector.class));
        dynamicDetectors = loader.loadDynamicServiceProviders(EncodingDetector.class);
        this.loader = loader;
    }

    public DefaultEncodingDetector(ServiceLoader loader,
                                   Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors) {
        super(loader.loadStaticServiceProviders(EncodingDetector.class), excludeEncodingDetectors);
        dynamicDetectors = loader.loadDynamicServiceProviders(EncodingDetector.class);
        Iterator<EncodingDetector> it = dynamicDetectors.iterator();
        while(it.hasNext()) {
            if (isExcluded(excludeEncodingDetectors, it.next().getClass())) {
                it.remove();
            }
        }
        this.loader = loader;
    }

    @Override
    public List<EncodingDetector> getDetectors() {
        if (loadDynamicServices) {
            dynamicDetectors = loader.loadDynamicServiceProviders(EncodingDetector.class);
        } 
        List<EncodingDetector> detectors = new ArrayList<>(dynamicDetectors);
        detectors.addAll(super.getDetectors());
        return detectors;
    }

}
