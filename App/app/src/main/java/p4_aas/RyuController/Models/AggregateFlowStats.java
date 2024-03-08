// Copyright 2023 riccardo.bacca@studio.unibo.it
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package p4_aas.RyuController.Models;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregateFlowStats {
    int flow_count;
    int packet_count;
    int byte_count;

    public int getFlow_count() {
        return flow_count;
    }
    public void setFlow_count(int flow_count) {
        this.flow_count = flow_count;
    }
    public int getPacket_count() {
        return packet_count;
    }
    public void setPacket_count(int packet_count) {
        this.packet_count = packet_count;
    }
    public int getByte_count() {
        return byte_count;
    }
    public void setByte_count(int byte_count) {
        this.byte_count = byte_count;
    }

    public SubmodelElement[] formatResult() {
        SubmodelElement flowCount = new Property("FlowCount", ValueType.Integer);
        SubmodelElement packetCount = new Property("PacketCount", ValueType.Integer);
        SubmodelElement byteCount = new Property("ByteCount", ValueType.Integer);

        flowCount.setKind(ModelingKind.TEMPLATE);
        packetCount.setKind(ModelingKind.TEMPLATE);
        byteCount.setKind(ModelingKind.TEMPLATE);

        flowCount.setValue("Flows: " + this.flow_count);
        packetCount.setValue("Packets: " + this.packet_count);
        byteCount.setValue("Bytes: " + this.byte_count);

        return new SubmodelElement[] {
            flowCount, packetCount, byteCount
        };
    }
}