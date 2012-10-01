/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.features.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.karaf.features.Feature;
import org.easymock.EasyMock;
import org.osgi.framework.Bundle;
import org.osgi.framework.startlevel.BundleStartLevel;

public class TestBase {
    public Bundle createDummyBundle(long id, final String symbolicName, Dictionary<String,String> headers) {
        Bundle bundle = EasyMock.createNiceMock(Bundle.class);
        
        // Be aware that this means all bundles are treated as different
        expect(bundle.compareTo(EasyMock.<Bundle>anyObject())).andReturn(1).anyTimes();

        expect(bundle.getBundleId()).andReturn(id).anyTimes();
        expect(bundle.getSymbolicName()).andReturn(symbolicName).anyTimes();
        expect(bundle.getHeaders()).andReturn(headers).anyTimes();
        BundleStartLevel sl = EasyMock.createMock(BundleStartLevel.class);
        expect(sl.isPersistentlyStarted()).andReturn(true).anyTimes();
        expect(bundle.adapt(BundleStartLevel.class)).andReturn(sl).anyTimes();
        replay(bundle, sl);
        return bundle;
    }
    
    public Dictionary<String, String> headers(String ... keyAndHeader) {
        Hashtable<String, String> headersTable = new Hashtable<String, String>();
        int c=0;
        while (c < keyAndHeader.length) {
            String key = keyAndHeader[c++];
            String value = keyAndHeader[c++];
            headersTable.put(key, value);
        }
        return headersTable;
    }
    
    public Map<String, Map<String, Feature>> features(Feature ... features) {
        final Map<String, Map<String, Feature>> featuresMap = new HashMap<String, Map<String,Feature>>();
        for (Feature feature : features) {
            Map<String, Feature> featureVersion = getOrCreate(featuresMap, feature);
            featureVersion.put(feature.getVersion(), feature);
        }
        return featuresMap;
    }
    
    private Map<String, Feature> getOrCreate(final Map<String, Map<String, Feature>> featuresMap, Feature feature) {
        Map<String, Feature> featureVersion = featuresMap.get(feature.getName());
        if (featureVersion == null) {
            featureVersion = new HashMap<String, Feature>();
            featuresMap.put(feature.getName(), featureVersion);
        }
        return featureVersion;
    }

    public Feature feature(String name, String version) {
        return new org.apache.karaf.features.internal.model.Feature(name, version);
    }
    
    public Set<Bundle> setOf(Bundle ... elements) {
        return new HashSet<Bundle>(Arrays.asList(elements));
    }
    
    public Set<Long> setOf(Long ... elements) {
        return new HashSet<Long>(Arrays.asList(elements));
    }

    @SuppressWarnings("unchecked")
    public void ignoreRefreshes(BundleManager bundleManager) {
        bundleManager.refreshBundles(EasyMock.anyObject(Set.class), EasyMock.anyObject(Set.class), EasyMock.anyObject(EnumSet.class));        
        EasyMock.expectLastCall().anyTimes();
    }
}
