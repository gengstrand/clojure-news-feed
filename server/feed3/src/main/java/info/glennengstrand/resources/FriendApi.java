/**
 * News Feed
 * news feed api
 *
 * OpenAPI spec version: 1.0.0
 * Contact: media@glennengstrand.info
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.glennengstrand.resources;

import com.google.inject.Inject;  
import javax.ws.rs.GET;  
import javax.ws.rs.POST;  
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.List;

import info.glennengstrand.api.Friend;
import io.dropwizard.jersey.params.LongParam;

@Path("/friends")
public class FriendApi {

   private final FriendApiService friendService;
	
   @Inject
   public FriendApi(FriendApiService friendService) {
      this.friendService = friendService;
   }
   
   @POST
   @Path("/new")
   @Consumes("application/json")
   @Produces("application/json")
  /**
   * create a new friendship
   * friends are those participants who receive news
   * @param body friendship to be created (required)
   * @return Friend
   */
   public Friend addFriend(Friend body) {
      return friendService.addFriend(body);
   }
   
   @GET
   @Path("/{id}")
   @Produces("application/json")
  /**
   * retrieve an individual friend
   * fetch a friend by id
   * @param id uniquely identifies the friend (required)
   * @return Friend
   */
   public List<Friend> getFriend(@PathParam("id") LongParam id) {
      return friendService.getFriend(id.get());
   }
   
   public static interface FriendApiService {
      Friend addFriend(Friend body);
      List<Friend> getFriend(Long id);
   }

}
