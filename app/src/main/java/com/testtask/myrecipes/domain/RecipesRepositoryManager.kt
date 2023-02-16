package com.testtask.myrecipes.domain

import com.testtask.myrecipes.data.network.ParserJson
import com.testtask.myrecipes.data.network.URLMaker
import com.testtask.myrecipes.domain.models.DataToDomainMapper
import com.testtask.myrecipes.domain.models.SingleRecipe

/**
 * короче, мы либо берем из репозитория, либо (доп.задача) из инета
 *
 * //следующий класс: упарвление интером - создаем ЮРЛ, передаем отправителю, получаем Гсон передаем
 * //расшифровщику, возвращаем распарсенную дату
 *
 * // управление репозиторием - делаем запрос, возвращаем распарсенную дату
 *
 * Класс, ответственный за упарвлением запросами составление URL запроса (в отдельном классе)
 * и направление его на сервер
 * На вход получает готовый URL запрос, возвращает Json ответ в формате String
 */

class RecipesRepositoryManager(
                        private val toaster: ToasterInterface,
                        private val addresses: Map<String, String>
                        ) {
    fun updateData(repository: RecipesRepositoryInterface): List<SingleRecipe> {
        // todo: logic with update method: network or local storage
        val URlType = "recipes.json"
        val request = URLMaker(addresses).makeURL("recipes")
        val resultData = updateDataFromNetwork(repository, URLrequest = request)
        
        return resultData
    }

    private fun updateDataFromNetwork(repository: RecipesRepositoryInterface, URLrequest: String): List<SingleRecipe> {
        val resultJson = repository.makeRewuest(URLrequest)
        val intermediateData = ParserJson().parseJson(resultJson)
        
        val resultData = mutableListOf<SingleRecipe>()
        val mapper = DataToDomainMapper()
        intermediateData.forEach{ recepie -> resultData.add(mapper.execute(recepie))}
        return resultData

        // todo: обработать вариант появления пустого результата
        // нам нужна построчная рецептов с постепенным заполнением? Или всей базы сразу?
        // если грузить всю базу сразу, во-первых, это долго во-вторых, сбой загрузки любой картинки - фейл?
        // из Jsonа возвращаем все загруженные строчки, и потом вставляем их в ливдату, каждый раз обновляя вью.
        
        // в итоге, из вью модели обновляем список, выводим его
        //      если картинки не все, показываем надпись loading...
        //      во вью модели создаем интерфейс для обновления ливдаты
        //      передаем интерфейс в PictureRepository с запросом на поиск картинок
        //      PictureRepository запускает кучу короутин, которые гуглят картинки, и обновляет вью
    }

}