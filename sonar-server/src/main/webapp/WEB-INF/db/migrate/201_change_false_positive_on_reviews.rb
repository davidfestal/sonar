#
# Sonar, entreprise quality control tool.
# Copyright (C) 2008-2011 SonarSource
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with Sonar; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#

#
# Sonar 2.9
#
class ChangeFalsePositiveOnReviews < ActiveRecord::Migration

  def self.up
    add_column 'reviews', 'false_positive', :boolean, :null => true, :default => false
    Review.reset_column_information
    
    Review.find(:all).each do |review|
      review.false_positive= (review.review_type == 'FALSE_POSITIVE')
      review.save!
    end
    
    remove_column 'reviews', 'review_type'
    Review.reset_column_information
  end

end
