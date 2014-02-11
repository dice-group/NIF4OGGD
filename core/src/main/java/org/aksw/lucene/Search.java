/**
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

package org.aksw.lucene;

import org.aksw.lucene.bean.Place;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

public interface Search {

    /**
     * Find streets by description
     *
     * @param hitsPerPage
     * @param indexPath
     * @param queryString
     * @return List of places
     * @throws java.io.IOException
     */
    public List<Place> findByDescription(Integer hitsPerPage, String indexPath, String queryString) throws IOException, ParseException;

    /**
     * Get docs by description
     * @param hitsPerPage
     * @param indexPath
     * @param queryString
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public List<Place> getDocsByDescription(Integer hitsPerPage, String indexPath, String queryString) throws IOException, ParseException;

}
