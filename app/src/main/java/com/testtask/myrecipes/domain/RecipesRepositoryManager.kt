package com.testtask.myrecipes.domain

import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageRepositoryInterface
import com.testtask.myrecipes.data.network.ParserJson
import com.testtask.myrecipes.data.network.URLConstantsSet
import com.testtask.myrecipes.data.network.URLMaker
import com.testtask.myrecipes.domain.models.DataToDomainMapper
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.ToasterInterface

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
    private val constantsURLSet: URLConstantsSet
                        ) {

    // по получаемому из активити репозиториям отрабатываем варианты загрузки
    fun updateData(
        repositoryFromNet: RecipesNetRepositoryInterface,
        repositoryFromStorage: RecipesStorageRepositoryInterface
    ): List<SingleRecipe> {
        // todo: logic with update method: network or local storage

        // переходим к запросу из сети
        val request = URLMaker(constantsURLSet).makeURLRecipesList()
        val resultData = updateDataFromNetwork(repositoryFromNet, URLrequest = request)
        
        return resultData
    }

    // тут обрабатывается вариант загрузки данных из сети
    private fun updateDataFromNetwork(repository: RecipesNetRepositoryInterface, URLrequest: String): List<SingleRecipe> {
        val resultJson = repository.makeRequest(URLrequest)
        val intermediateData = ParserJson().parseJson(resultJson)
        
        val resultData = mutableListOf<SingleRecipe>()
        val mapper = DataToDomainMapper()
        intermediateData.forEach{ recepie -> resultData.add(mapper.execute(recepie))}
        return resultData

        // todo: обработать вариант появления пустого результата
        // в итоге, из вью модели обновляем список, выводим его
        //      если картинки не все, показываем надпись loading...
        //      во вью модели создаем интерфейс для обновления ливдаты
        //      передаем интерфейс в PictureRepository с запросом на поиск картинок
        //      PictureRepository запускает кучу короутин, которые гуглят картинки, и обновляет вью
    }

    private fun updateDataFromStorage(repository: RecipesStorageRepositoryInterface){

    }

}