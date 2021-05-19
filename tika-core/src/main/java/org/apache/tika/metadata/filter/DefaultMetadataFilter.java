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
package org.apache.tika.metadata.filter;

import java.util.List;
import org.apache.tika.config.LoadErrorHandler;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultMetadataFilter extends CompositeMetadataFilter {

    private ServiceLoader serviceLoader;
    private boolean loadDynamicServices;


    private static List<MetadataFilter> getDefaultFilters(
            ServiceLoader loader) {
        List<MetadataFilter> detectors = loader.loadStaticServiceProviders(MetadataFilter.class);
        ServiceLoaderUtils.sortLoadedClasses(detectors);

        return detectors;
    }

    public DefaultMetadataFilter(ServiceLoader serviceLoader) {
        super(getDefaultFilters(serviceLoader));
        this.serviceLoader = serviceLoader;
    }

    public DefaultMetadataFilter(List<MetadataFilter> metadataFilters) {
        super(metadataFilters);
    }

    public DefaultMetadataFilter(boolean loadDynamicServices) {
        this(new ServiceLoader(loadDynamicServices));
        this.loadDynamicServices = loadDynamicServices;
    }

    public DefaultMetadataFilter() {
        this(new ServiceLoader());
    }

    /** {@inheritDoc} */
    @Override
    protected List<MetadataFilter> getFilters() {
        if (loadDynamicServices) {
            List<MetadataFilter> filters = serviceLoader.loadDynamicServiceProviders(MetadataFilter.class);
            filters.addAll(super.getFilters());
            return filters;
        }
        return super.getFilters();
    }
}
