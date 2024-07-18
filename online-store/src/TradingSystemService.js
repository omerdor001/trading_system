// src/services/TradingSystemService.js

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/trading'; // Adjust URL as per your backend configuration

const TradingSystemService = {
    deleteInstance() {
        return axios.delete(`${API_BASE_URL}/instance`);
    },

    openSystem() {
        return axios.post(`${API_BASE_URL}/openSystem`);
    },

    closeSystem(username, token) {
        return axios.post(`${API_BASE_URL}/closeSystem`, { params: { username, token } });
    },

    enter() {
        return axios.get(`${API_BASE_URL}/enter`);
    },

    exit(token, username) {
        return axios.post(`${API_BASE_URL}/exit`, { params: { token, username } });
    },

    register(username, password, birthday) {
        return axios.get(`${API_BASE_URL}/register`, { params: { username, password, birthday } });
    },

    closeStoreExist(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/close`, { params: { username, token, storeName } });
    },

    openStoreExist(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/open`, { params: { username, token, storeName } });
    },

    openStore(username, token, storeName, description) {
        return axios.post(`${API_BASE_URL}/store/create`, { username, token, storeName, description });
    },

    addProduct(username, token, productId, storeName, productName, productDescription, productPrice, productQuantity, rating, category, keyWords) {
        return axios.post(`${API_BASE_URL}/product/add`, {
            username,
            token,
            productId,
            storeName,
            productName,
            productDescription,
            productPrice,
            productQuantity,
            rating,
            category,
            keyWords
        });
    },

    removeProduct(username, token, storeName, productId) {
        return axios.delete(`${API_BASE_URL}/product/remove`, { params: { username, token, storeName, productId } });
    },

    getStoreProducts(userName, token, store_name) {
        return axios.get(`${API_BASE_URL}/getStoreProducts`, { params: { userName, token, store_name } });
    },

    setProductName(username, token, storeName, productId, productName) {
        return axios.post(`${API_BASE_URL}/setProductName`, { username, token, storeName, productId, productName });
    },
    setProductDescription(username, token, storeName, productId, productDescription) {
        return axios.post(`${API_BASE_URL}/setProductDescription`, { username, token, storeName, productId, productDescription });
    },

    setProductPrice(username, token, storeName, productId, productPrice) {
        return axios.post(`${API_BASE_URL}/setProductPrice`, { username, token, storeName, productId, productPrice });
    },

    setProductQuantity(username, token, storeName, productId, productQuantity) {
        return axios.post(`${API_BASE_URL}/setProductQuantity`, { username, token, storeName, productId, productQuantity });
    },

    setRating(username, token, storeName, productId, rating) {
        return axios.post(`${API_BASE_URL}/setRating`, { username, token, storeName, productId, rating });
    },

    setCategory(username, token, storeName, productId, category) {
        return axios.post(`${API_BASE_URL}/setCategory`, { username, token, storeName, productId, category });
    },
    login(token, usernameV, username, password) {
        return axios.post(`${API_BASE_URL}/login`, { token, usernameV, username, password });
    },

    logout(token, username) {
        return axios.post(`${API_BASE_URL}/logout`, { token, username });
    },

    suspendUser(token, admin, toSuspend, endSuspension) {
        return axios.post(`${API_BASE_URL}/suspendUser`, { token, admin, toSuspend, endSuspension });
    },

    endSuspendUser(token, admin, toSuspend) {
        return axios.post(`${API_BASE_URL}/endSuspendUser`, { token, admin, toSuspend });
    },

    setAddress(username, token, address) {
        return axios.post(`${API_BASE_URL}/setAddress`, { username, token, address });
    },

    watchSuspensions(token, admin) {
        return axios.get(`${API_BASE_URL}/watchSuspensions`, { params: { token, admin } });
    },

    suggestOwner(appoint, token, newOwner, storeName) {
        return axios.post(`${API_BASE_URL}/suggestOwner`, { appoint, token, newOwner, storeName });
    },

    suggestManage(appoint, token, newManager, storeName, watch, editSupply, editBuyPolicy, editDiscountPolicy) {
        return axios.post(`${API_BASE_URL}/suggestManage`, {
            appoint,
            token,
            newManager,
            storeName,
            watch,
            editSupply,
            editBuyPolicy,
            editDiscountPolicy
        });
    },

    approveManage(newManager, token, storeName, appoint, watch, editSupply, editBuyPolicy, editDiscountPolicy) {
        return axios.post(`${API_BASE_URL}/approveManage`, {
            newManager,
            token,
            storeName,
            appoint,
            watch,
            editSupply,
            editBuyPolicy,
            editDiscountPolicy
        });
    },

    approveOwner(newOwner, token, storeName, appoint) {
        return axios.post(`${API_BASE_URL}/approveOwner`, { newOwner, token, storeName, appoint });
    },

    rejectToOwnStore(username, token, storeName, appoint) {
        return axios.post(`${API_BASE_URL}/rejectToOwnStore`, { username, token, storeName, appoint });
    },
    rejectToManageStore(userName, token, storeNameId, appoint) {
        return axios.post(`${API_BASE_URL}/rejectToManageStore`, { userName, token, store_name_id: storeNameId, appoint });
    },

    waiverOnOwnership(userName, token, storeName) {
        return axios.post(`${API_BASE_URL}/waiverOnOwnership`, { userName, token, storeName });
    },

    fireManager(owner, token, storeName, manager) {
        return axios.post(`${API_BASE_URL}/fireManager`, { owner, token, storeName, manager });
    },

    fireOwner(ownerAppoint, token, storeName, ownerToFire) {
        return axios.post(`${API_BASE_URL}/fireOwner`, { ownerAppoint, token, storeName, ownerToFire });
    },

    editPermissionForManager(username, token, managerToEdit, storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy) {
        return axios.post(`${API_BASE_URL}/store/manager/permission/edit`, {
            username,
            token,
            managerToEdit,
            storeNameId,
            watch,
            editSupply,
            editBuyPolicy,
            editDiscountPolicy
        });
    },

    getAllStores(userName, token) {
        return axios.get(`${API_BASE_URL}/stores`, { params: { userName, token } });
    },

    getProductInfo(userName, token, storeName, productId) {
        return axios.get(`${API_BASE_URL}/product/info`, { params: { userName, token, store_name: storeName, product_Id: productId } });
    },

    searchNameInStore(userName, productName, token, storeName, minPrice, maxPrice, minRating, category) {
        return axios.get(`${API_BASE_URL}/store/search/name`, {
            params: {
                userName,
                productName,
                token,
                store_name: storeName,
                minPrice,
                maxPrice,
                minRating,
                category
            }
        });
    },

    searchCategoryInStore(userName, token, category, storeName, minPrice, maxPrice, minRating) {
        return axios.get(`${API_BASE_URL}/store/search/category`, {
            params: {
                userName,
                token,
                category,
                store_name: storeName,
                minPrice,
                maxPrice,
                minRating
            }
        });
    },

    searchKeywordsInStore(userName, token, keyWords, storeName, minPrice, maxPrice, minRating, category) {
        return axios.get(`${API_BASE_URL}/store/search/keywords`, {
            params: {
                userName,
                token,
                keyWords,
                store_name: storeName,
                minPrice,
                maxPrice,
                minRating,
                category
            }
        });
    },

    searchNameInStores(userName, token, productName, minPrice, maxPrice, minRating, category, storeRating) {
        return axios.get(`${API_BASE_URL}/stores/search/name`, {
            params: {
                userName,
                token,
                productName,
                minPrice,
                maxPrice,
                minRating,
                category,
                storeRating
            }
        });
    },

    searchCategoryInStores(userName, token, category, minPrice, maxPrice, minRating, storeRating) {
        return axios.get(`${API_BASE_URL}/stores/search/category`, {
            params: {
                userName,
                token,
                category,
                minPrice,
                maxPrice,
                minRating,
                storeRating
            }
        });
    },

    searchKeywordsInStores(userName, token, keyWords, minPrice, maxPrice, minRating, category, storeRating) {
        return axios.get(`${API_BASE_URL}/stores/search/keywords`, {
            params: {
                userName,
                token,
                keyWords,
                minPrice,
                maxPrice,
                minRating,
                category,
                storeRating
            }
        });
    },
    approvePurchase(username, token, address, amount, currency, cardNumber, month, year, holder, ccv, id) {
        return axios.post(`${API_BASE_URL}/purchase/approve`, null, {
            params: {
                username,
                token,
                address,
                amount,
                currency,
                cardNumber,
                month,
                year,
                holder,
                ccv,
                id
            }
        });
    },

    getPurchaseHistory(username, token, storeName) {
        return axios.get(`${API_BASE_URL}/purchase/history`, { params: { username, token, storeName } });
    },

    // Cart endpoints
    addToCart(username, token, productId, storeName, quantity) {
        return axios.post(`${API_BASE_URL}/cart/add`, { username, token, productId, storeName, quantity });
    },

    removeFromCart(username, token, productId, storeName, quantity) {
        return axios.post(`${API_BASE_URL}/cart/remove`, { username, token, productId, storeName, quantity });
    },

    viewCart(username, token) {
        return axios.get(`${API_BASE_URL}/cart/view`, { params: { username, token } });
    },

    // Purchase history endpoints
    getAllHistoryPurchases(userName, token, storeName) {
        return axios.get(`${API_BASE_URL}/purchase/history/all`, { params: { userName, token, storeName } });
    },

    getHistoryPurchasesByCustomer(userName, token) {
        return axios.get(`${API_BASE_URL}/purchase/history/customer`, { params: { username, token } });
    },

    // Store officials information endpoints
    requestInformationAboutOfficialsInStore(userName, token, storeName) {
        return axios.get(`${API_BASE_URL}/store/officials/info`, { params: { userName, token, storeName } });
    },

    requestManagersPermissions(userName, token, storeName) {
        return axios.get(`${API_BASE_URL}/store/manager/permissions`, { params: { userName, token, storeName } });
    },

    requestInformationAboutSpecificOfficialInStore(userName, token, storeName, officialUserName) {
        return axios.get(`${API_BASE_URL}/store/official/info`, { params: { userName, token, storeName, officialUserName } });
    },

    // Cart price calculation endpoint
    calculatePrice(username, token) {
        return axios.post(`${API_BASE_URL}/cart/price/calculate`, { username, token });
    },

    // Discount creation endpoints
    getDiscountPolicies(username, token, storeName) {
        return axios.get(`${API_BASE_URL}/store/${storeName}/discount-policies`, { params: { username, token } });
    },

    getDiscountConditions(username, token, storeName) {
        return axios.get(`${API_BASE_URL}/store/${storeName}/discount-conditions`, { params: { username, token } });
    },

    addCategoryPercentageDiscount(username, token, storeName, category, discountPercent) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/category-percentage`, { username, token, category, discountPercent });
    },

    addProductPercentageDiscount(username, token, storeName, productId, discountPercent) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/product-percentage`, { username, token, productId, discountPercent });
    },

    addStoreDiscount(username, token, storeName, discountPercent) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/store`, { username, token, discountPercent });
    },

    addConditionalDiscount(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/conditional`, { username, token });
    },

    addAdditiveDiscount(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/additive`, { username, token });
    },

    addMaxDiscount(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/max`, { username, token });
    },

    addCategoryCountCondition(username, token, storeName, category, count) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/conditions/category-count`, { username, token, category, count });
    },
    addTotalSumCondition(username, token, storeName, requiredSum) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/conditions/total-sum`, { username, token, requiredSum });
    },

    addProductCountCondition(username, token, storeName, productId, count) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/conditions/product-count`, { username, token, productId, count });
    },

    addAndDiscount(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/and`, { username, token });
    },

    addOrDiscount(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/or`, { username, token });
    },

    addXorDiscount(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/discounts/xor`, { username, token });
    },

    removeDiscount(username, token, storeName, selectedIndex) {
        return axios.delete(`${API_BASE_URL}/store/${storeName}/discounts/${selectedIndex}`, { params: { username, token } });
    },

    // Purchase policies
    getPurchasePoliciesInfo(username, token, storeName) {
        return axios.get(`${API_BASE_URL}/store/${storeName}/purchase-policies`, { params: { username, token } });
    },

    addPurchasePolicyByAge(username, token, storeName, ageToCheck, category) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/age`, { username, token, ageToCheck, category });
    },

    addPurchasePolicyByCategoryAndDate(username, token, storeName, category, dateTime) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/category-date`, { username, token, category, dateTime });
    },

    addPurchasePolicyByDate(username, token, storeName, dateTime) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/date`, { username, token, dateTime });
    },

    addPurchasePolicyByProductAndDate(username, token, storeName, productId, dateTime) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/product-date`, { username, token, productId, dateTime });
    },

    addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, productId, numOfQuantity) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/cart-max-products`, { username, token, productId, numOfQuantity });
    },

    addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, numOfQuantity) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/cart-min-products`, { username, token, numOfQuantity });
    },

    addPurchasePolicyByShoppingCartMinProductsUnit(username, token, storeName, productId, numOfQuantity) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/cart-min-products-unit`, { username, token, productId, numOfQuantity });
    },

    addAndPurchasePolicy(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/and`, { username, token });
    },

    addOrPurchasePolicy(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/or`, { username, token });
    },

    addConditioningPurchasePolicy(username, token, storeName) {
        return axios.post(`${API_BASE_URL}/store/${storeName}/purchase-policies/conditioning`, { username, token });
    },

    setPurchasePolicyProductId(username, token, storeName, selectedIndex, productId) {
        return axios.put(`${API_BASE_URL}/store/${storeName}/purchase-policies/${selectedIndex}/product-id`, { username, token, productId });
    },

    setPurchasePolicyNumOfQuantity(username, token, storeName, selectedIndex, numOfQuantity) {
        return axios.put(`${API_BASE_URL}/store/${storeName}/purchase-policies/${selectedIndex}/quantity`, { username, token, numOfQuantity });
    },

    setPurchasePolicyDateTime(username, token, storeName, selectedIndex, dateTime) {
        return axios.put(`${API_BASE_URL}/store/${storeName}/purchase-policies/${selectedIndex}/date`, { username, token, dateTime });
    },

    setPurchasePolicyAge(username, token, storeName, selectedIndex, age) {
        return axios.put(`${API_BASE_URL}/store/${storeName}/purchase-policies/${selectedIndex}/age`, { username, token, age });
    },

    setFirstPurchasePolicy(username, token, storeName, selectedDiscountIndex, selectedFirstIndex) {
        return axios.put(`${API_BASE_URL}/store/${storeName}/purchase-policies/${selectedDiscountIndex}/first`, { username, token, selectedFirstIndex });
    },

    setSecondPurchasePolicy(username, token, storeName, selectedDiscountIndex, selectedSecondIndex) {
        return axios.put(`${API_BASE_URL}/store/${storeName}/purchase-policies/${selectedDiscountIndex}/second`, { username, token, selectedSecondIndex });
    },

    removePurchasePolicy(username, token, storeName, selectedIndex) {
        return axios.delete(`${API_BASE_URL}/store/${storeName}/purchase-policies/${selectedIndex}`, { params: { username, token } });
    }

};


export default TradingSystemService;
