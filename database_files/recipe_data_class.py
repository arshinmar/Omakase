import pandas as pd
import numpy as np
import pickle
import ast
import requests
import re
import json
import warnings
import time

class recipe_data_class:
    def __init__(self, raw_recipes_file, pp_recipes_file, ingr_map_file):
        self.raw_recipes_file = raw_recipes_file
        self.pp_recipes_file = pp_recipes_file
        self.ingr_map_file = ingr_map_file
        self.raw_recipes = None
        self.pp_recipes = None
        self.ingr_map = None
        self.parts = 4
        #self.loadCSVs(raw_recipes_file, pp_recipes_file, ingr_map_file, cache=True)
        #self.loadFromCache(raw_recipes_file, pp_recipes_file, ingr_map_file)

        #self.splitDataFrame(raw_recipes_file, pp_recipes_file)
        self.loadFromSplitDataFrames(raw_recipes_file, pp_recipes_file, ingr_map_file)

        self.forbidden = ['meal','liquid','green']
        self.nutrition_cols = ['calories', 'total fat', 'sugar', 'sodium', 'protein', 'saturated fat', 'carbohydrates']

    def loadCSVs(self, raw_recipes_file, pp_recipes_file, ingr_map_file, cache=False):
        #open ingredient map file
        with open(ingr_map_file, 'rb') as f:
            self.ingr_map = pickle.load(f)

        #load the pp_recipes_file into a pd.DataFrame; convert entries in 'ingredient_ids' column to sets
        self.pp_recipes = pd.read_csv(pp_recipes_file, converters={'ingredient_ids': self.__stringlist_to_set})

        #load the raw_recipes_file into a pd.DataFrame
        self.raw_recipes = pd.read_csv(raw_recipes_file)

        if cache == True: #save the data as pkl file
            self.pp_recipes.to_pickle(pp_recipes_file[0:-4] + '.pkl')
            self.raw_recipes.to_pickle(raw_recipes_file[0:-4] + '.pkl')
        return True

    def loadFromCache(self, raw_recipes_file, pp_recipes_file, ingr_map_file):
        #open ingredient map file
        with open(ingr_map_file, 'rb') as f:
            self.ingr_map = pickle.load(f)

        #load the pp_recipes_file into a pd.DataFrame
        self.pp_recipes = pd.read_pickle(pp_recipes_file)

        #load the raw_recipes_file into a pd.DataFrame
        self.raw_recipes = pd.read_pickle(raw_recipes_file)
        return True

    def splitDataFrame(self, raw_recipes_file, pp_recipes_file, parts=4):
        self.parts = parts
        split_raw_recipes = np.array_split(self.raw_recipes, self.parts)
        for i in range(0,self.parts,1):
            split_raw_recipes[i].to_pickle(raw_recipes_file[0:-4] + '_' + str(i+1) + '.pkl')

        split_pp_recipes = np.array_split(self.pp_recipes, self.parts)
        for i in range(0,self.parts,1):
            split_pp_recipes[i].to_pickle(pp_recipes_file[0:-4] + '_' + str(i+1) + '.pkl')
        return True

    def loadFromSplitDataFrames(self, raw_recipes_file, pp_recipes_file, ingr_map_file, parts=4):
        self.parts = parts
        raw_dfs = []
        for i in range(1,self.parts+1,1):
            raw_dfs += [pd.read_pickle(raw_recipes_file[0:-4] + '_' + str(i) + '.pkl')]

        self.raw_recipes = pd.concat(raw_dfs)
        pp_dfs = []
        for i in range(1,self.parts+1,1):
            pp_dfs += [pd.read_pickle(pp_recipes_file[0:-4] + '_' + str(i) + '.pkl')]
        self.pp_recipes = pd.concat(pp_dfs)

        with open(ingr_map_file, 'rb') as f:
            self.ingr_map = pickle.load(f)

    def __stringlist_to_set(self,s):
        """
        Given input string s in list format, convert to set
        """
        return set(ast.literal_eval(s))

    ###########################################################################

    def get_id(self, ingr):
        """
        Given an ingredient ingr which is a str, returns the id of the ingredient
        in the dataset. If ingr does not exist, return None
        """
        ingr = ingr.lower() #convert to words all lower case
        if ingr in self.forbidden:
            return None
        df_part = self.ingr_map.loc[self.ingr_map['processed'] == ingr]
        if df_part.empty == True: #if DataFrame is empty, try looking at 'raw_ingr'
            df_part = self.ingr_map.loc[self.ingr_map['raw_ingr'] == ingr]

        #print(df_part, "\n-------------")
        if df_part.empty == True: #if DataFrame is empty, ingredient doesn't exist
            return None
        else:
            return df_part['id'].to_numpy()[0] #convert the pd.Series to numpy array and take first element

    def get_ids(self, ingrs):
        """
        Given a list of ingredients ingrs, returns a list of ingredient ids
        and a list where each element corresponds to whether an ingredient was
        found/included. If ingredient does not exist, then the corresponding
        element in the list included is False
        """
        r = []
        included = [True for i in range(len(ingrs))] #whether or not ingredient is included in return
        for i in range(0,len(ingrs),1):
            if type(ingrs[i]) == str:
                ingr_id = self.get_id(ingrs[i])
                r += [ingr_id]
                if ingr_id == None: #if ingredient doesn't exist
                    included[i] = False
            else:
                included[i] = False

        return r, included

    def get_processed_ingr_name(self, ingr):
        """
        Given an ingredient ingr which is a str, returns the name of the ingredient
        in the dataset. If ingr does not exist, return None
        """
        ingr = ingr.lower() #convert to words all lower case
        if ingr in self.forbidden:
            return None
        df_part = self.ingr_map.loc[self.ingr_map['processed'] == ingr]
        if df_part.empty == True: #if DataFrame is empty, try looking at 'raw_ingr'
            df_part = self.ingr_map.loc[self.ingr_map['raw_ingr'] == ingr]
            if df_part.empty == False: #if candidate ingredient exists
                return df_part['raw_ingr'].to_list()[0]
            else:
                return None
        else:
            return df_part['processed'].to_numpy()[0] #convert the pd.Series to list and take first element

    def get_ingr_processed(self, input_list):
        """
        Given a list of lists ingrs_listoflists of candidate ingredients for each
        object, returns a list ingr_processed containing the predicted actual
        ingredients, and a capitalized version of list ingr_processed which is
        list ingr_processed_for_display
        """
        ingr_processed = []
        for listoflists in input_list:
            ingr_text = listoflists[1]
            found_with_text = False
            if len(ingr_text) != 0: #if there is text detected
                keep = ""
                for length in range(len(ingr_text)-1,0,-1):
                    for start_idx in range(0,len(ingr_text)-length+1,1): #sliding window; start_idx is starting index
                        word = ""
                        for i in range(start_idx,start_idx+length,1):
                            word = word + ingr_text[i] + " "
                        word = word[0:-1] #remove space at the end
                        name_temp = self.get_processed_ingr_name(word)
                        if name_temp != None: #ingredient exists
                            keep = name_temp
                            found_with_text = True
                            break
                    if found_with_text == True:
                        break
                if keep != "":
                    ingr_processed += [keep]

            if found_with_text == False: #if ingredient was not found in text
                ingr_list = listoflists[0]
                if len(ingr_list) != 0:
                    ingr_list_clean = []
                    for candidate in ingr_list:
                        name = self.get_processed_ingr_name(candidate)
                        if name != None: #ingredient exists
                            ingr_list_clean += [name]
                    found = False
                    for i in range(0,len(ingr_list_clean),1):
                        for j in range(i+1,len(ingr_list_clean),1): #check remaining elements
                            if ingr_list_clean[i] in ingr_list_clean[j]:
                                ingr_processed += [ingr_list_clean[j]]
                                found = True
                                break
                        if found == False:
                            ingr_processed += [ingr_list_clean[i]]
                            break
                    if len(ingr_list_clean) == 0:
                        ingr_processed += [None]

        ingr_processed_for_display = []
        for ingr in ingr_processed: #capitalize each element in ingr_list_clean
            if type(ingr) == str:
                ingr_processed_for_display += [ingr.capitalize()]
            else:
                ingr_processed_for_display += [None]
        return ingr_processed, ingr_processed_for_display

    ###########################################################################
    # Core functions

    def getRecipesSetForIngr(self, ingr_num):
        class dummy:
        #This class is merely so that pd.Series.map() could be used later on
            def __init__(self, n):
                self.n = n
            def isin(self,thing):
                return self.n in thing

        obj = dummy(ingr_num) #initialize dummy class so that dummy.isin can be called in next line

        #select the rows in which ingr_num is in an entry (set) in the 'ingredient_ids' column, and assign to pp_recipes_filtered (pd.DataFrame)
        pp_recipes_filtered = self.pp_recipes.loc[self.pp_recipes['ingredient_ids'].map(obj.isin) == True]

        #convert pp_recipes_filtered to list, and then convert to a set
        return set(pp_recipes_filtered['id'].tolist())

    def getRecipesSet(self, ingr_list, mandatory=None):
        """
        Given a list of ingredients ingr_list, return a set of recipes
        Ingredients that do not exist have value None in ingr_list
        """
        recipes_sets = [] #list that stores sets of recipes for each ingredient
        included = [True for i in range(len(ingr_list))] #whether or not ingredient is included
        set_lengths = [] #list that stores the length of each set of recipes for each ingredient
        for i in range(0,len(ingr_list),1):
            if ingr_list[i] != None: #if ingredient doesn't exist
                recipes_sets += [self.getRecipesSetForIngr(ingr_list[i])]
            else:
                recipes_sets += [set()]
            set_lengths += [len(recipes_sets[i])]

        if len(recipes_sets) == 0:
            return set(), included
        elif len(recipes_sets) == 1:
            return recipes_sets[0], included

        #order is a list of the indices of set_lengths for a decreasing sort
        order = sorted(range(len(set_lengths)), key=lambda k: -set_lengths[k])

        r_set = recipes_sets[order[0]] #r_set := set of recipes with largest size
        for i in range(1,len(order),1):
            temp = r_set.intersection(recipes_sets[order[i]])
            if len(temp) != 0:
                r_set = temp
            else:
                included[order[i]] = False

        return r_set, included

    ###########################################################################

    def getRecipeDataFrameFromID(self, recipe_id):
        """
        Returns a pd.DataFrame corresponding to recipe_id
        """
        return self.raw_recipes.loc[self.raw_recipes['id'] == recipe_id]

    def constructRecipeDictFromID(self, recipe_id):
        """
        Given some recipe_id, returns a dictionary representing that recipe
        """
        df = self.getRecipeDataFrameFromID(recipe_id)
        df_dict = df.to_dict(orient='records')[0]
        d = dict()
        name = ' '.join(df_dict['name'].split())
        d['name'] = name.title() #str
        d['id'] = df_dict['id'] #int

        url = self.getRecipeImage(recipe_id)
        if url != None: #if url does not exist
            d['image_url'] = url
        else:
            d['image_url'] = None

        time_to_cook = df_dict['minutes']
        if time_to_cook > 24*60: #if it takes more than 24 hours
            d['time'] = None
        else:
            d['time'] = time_to_cook

        nutrition = dict()
        nutrition_list = ast.literal_eval(df_dict['nutrition'])
        for i in range(0,len(self.nutrition_cols),1):
            nutrition[self.nutrition_cols[i]] = nutrition_list[i]
        d['nutrition'] = nutrition

        ingredients = ast.literal_eval(df_dict['ingredients'])
        for i in range(0,len(ingredients),1):
            ingredients[i] = ingredients[i].capitalize()
        d['ingredients'] = ingredients

        steps = ast.literal_eval(df_dict['steps'])
        for i in range(0,len(steps),1):
            steps[i] = steps[i].capitalize()
        d['steps'] = steps
        return d

    def dictToJSON(self, d, filename=None):
        if filename != None:
            with open(filename,'w') as f:
                json.dump(d, f)
        return json.dumps(d)

    def getDictOfRecipes(self, ingrs, filename=None):
        r_set, _ = self.getRecipeNamesFromIngrList(ingrs)
        recipe_dicts = []
        for recipe_id in r_set:
            recipe_dicts += [self.constructRecipeDictFromID(recipe_id)]
        return self.dictToJSON({'recipes':recipe_dicts},filename=filename)

    ###########################################################################

    def getRecipeNamesFromIngrList(self, ingrs):
        ingr_list, _ = self.get_ids(ingrs)
        r_set, included = self.getRecipesSet(ingr_list)
        r_names = []
        for elem in r_set:
            r_names += [self.getRecipeDataFrameFromID(elem)['name'].to_string(index=False).strip()]
        return r_set, r_names

    def getRecipesFromRaw(self, nested_list):
        _, ingr_list = self.get_ingr_processed(nested_list)
        return self.getRecipeNamesFromIngrList(ingr_list)

    ###########################################################################

    def getRecipeImage(self, recipe_id):
        """
        Given the recipe_id, returns the url of the image of the recipe
        Note that on the food.com webpages, the image of the recipes have the format:
        <meta data-n-head="true" name="og:image" content="https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/22/72/26/picIHVDoN.jpg">
        """
        if type(recipe_id) != int:
            return None
        try:
            website = requests.get('https://www.food.com/' + str(recipe_id))
        except:
            warnings.warn("ERROR: Could not get request")
            return None
        html = website.text
        pat = re.compile(r'name="og:image" content="([^"]+)') #Use regular expression
        img = pat.findall(html)
        if len(img) == 0:
            return None
        else:
            return img[0]

#Sample Code
'''
if __name__ == '__main__':
    raw_recipes_file = "RAW_recipes.pkl"
    pp_recipes_file = "PP_recipes.pkl"
    ingr_map_file = 'ingr_map.pkl'

    st = time.time()
    data_obj = recipe_data_class(raw_recipes_file, pp_recipes_file, ingr_map_file)
    et = time.time()
    print("Loading took", et - st, "seconds")

    input_list = [[['Blue', 'Green', 'Aqua', 'Daytime', 'Sky', 'Black', 'Turquoise', 'Azure', 'Teal', 'Atmosphere'], []], [['Meal', 'Mason jar', 'Breakfast', 'Granola', 'Cuisine', 'Dish', 'Snack', 'Ingredient', 'Breakfast cereal', 'Container', 'Container'], []], [['Mason jar', 'Side dish', 'Ingredient', 'Cuisine', 'Container', 'Container', 'Container'], []], [['License plate'], []], [['Mason jar', 'Food storage containers', 'Ingredient', 'Glass', 'Cuisine', 'Container', 'Container', 'Container'], []], [['Mason jar', 'Food storage containers', 'Drinkware', 'Glass', 'Petal', 'Meal', 'Cuisine', 'Tableware', 'Breakfast', 'Container', 'Container', 'Container'], []], [['Product', 'Mason jar', 'Food storage containers', 'Drinkware', 'Glass', 'Cuisine', 'Vegetarian food', 'Tableware', 'Cookie jar', 'Container', 'Tableware', 'Container', 'Container'], []], [['Glass', 'Drinkware', 'Bottle'], []], [['Product', 'Cuisine', 'Ingredient', 'Packaged goods', 'Container', 'Packaged goods'], ['SESAME', 'SEEDS']], [['Packaged goods', 'Container', 'Packaged goods'], ['SESAME', 'SEEDE']], [['Packaged goods', 'Packaged goods', 'Container'], ['SESAME', 'SEEDE']], [['Cuisine', 'Granola', 'Dish', 'Snack', 'Ingredient', 'Meal', 'Breakfast', 'Breakfast cereal', 'Muesli', 'Container', 'Packaged goods'], ['WALNUTS']], [['Blue', 'Black', 'Green', 'Daytime', 'Sky', 'Text', 'Grey', 'Azure', 'Brown', 'Turquoise'], []], [['Product', 'Liquid', 'Paint', 'Packaged goods', 'Packaged goods'], ['URE', 'NILLA', 'HE', 'DAILY', 'TR', 'M.']], [['Ingredient', 'Bird supply', 'Cuisine', 'Packaged goods', 'Bottle', 'Container'], ['ma']], [['Product', 'Joint', 'Material property', 'Wood stain', 'Metal', 'Packaged goods', 'Packaged goods', 'Packaged goods'], ['Sun', 'Potion', 'ternaten', 'ha', 'MATCHA']], [['Product', 'Beauty', 'Material property', 'Skin care', 'Packaged goods', 'Packaged goods', 'Packaged goods'], ['Su', 'МАТCHA']], [['Auto part', 'Paint', 'Packaged goods', 'Packaged goods', 'Packaged goods'], ['MR']], [['Luggage & bags', 'Luggage & bags', 'Luggage & bags'], []], [['Automotive exterior', 'Vehicle', 'Automotive design', 'Race car', 'Radio-controlled toy', 'Car', 'Motorsport'], []], [['Red', 'Orange', 'Peach', 'Sky'], []], [['Product', 'Packaged goods', 'Packaged goods'], ['EROBIOTIC', 'wildbrin']], [['Packaged goods', 'Container'], ['SALSA', 'PROBIOTIC', 'wildbrir', 'te']], [['Packaged goods', 'Container'], ['SALSA', 'PROBIOTIC', 'wildbrir']], [['Yellow', 'Material property', 'Packaged goods', 'Packaged goods', 'Packaged goods'], ['HOPE', 'O', 'ORIGINAL', 'RECI']], [['Breakfast cereal', 'Meal', 'Granola', 'Snack', 'Cuisine', 'Breakfast', 'Dish', 'Ingredient', 'Vegetarian food', 'Tableware', 'Container'], []], [['Ingredient', 'Cuisine', 'Packaged goods', 'Container', 'Container'], ['CAFE', 'Vumim', 'NAL', 'YUMM!', 'SAUCE']], [['Yellow', 'Technology', 'Packaged goods', 'Packaged goods'], ['O', 'ORIGINAL', 'RECI', 'www.', 'kite', 'hill']], [['Vehicle', 'Container'], []], [['Product', 'Bottle', 'Liquid', 'Packaged goods', 'Packaged goods'], ['BUR', 'ORGANIC', 'MAMENTED', 'HOT', 'SAUCI', 'Oge']], [['Product', 'Liquid', 'Bottle', 'Fluid', 'Paint', 'Packaged goods', 'Packaged goods'], ['BUR', 'ORGANIC', 'NMENTED', 'HOT', 'SAUCI', 'ge']], [['Packaged goods', 'Bottle', 'Container'], ['CASS', 'SEAIN', 'LAS', 'NIC', 'ITED', 'UCE', 'NO', 'TIME', 'TO', 'EAT?', 'GRAB', 'SOME', 'Dales', 'RANCHO', 'MELADUCO', 'Danle', 'Jaren']], [['Food storage containers', 'Lunch', 'Container', 'Container', 'Container'], []], [['Still life photography', 'Still life', 'Clementine', 'Vegetarian food', 'Painting', 'Orange', 'Citrus', 'Painting'], []]]
    _, ingr_list = data_obj.get_ingr_processed(input_list)

    j = data_obj.getDictOfRecipes(ingr_list, filename='recipes.json')
    print(j)

    #recipes = data_obj.getRecipesFromRaw(input_list)
    #print(recipes)
    #print(data_obj.constructRecipeDictFromID(99305))
'''
