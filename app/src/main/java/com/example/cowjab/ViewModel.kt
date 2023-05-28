package com.example.cowjab

import java.time.LocalDate
import java.time.temporal.ChronoUnit


class CoreViewModel {

    var runningID: Int = 100

    var technicians = mutableListOf<TechnicianModel>(
        TechnicianModel(
            69, "QR69", "Hemi",
            "074 9095 4037", "hemi@gmail.com",
            "Whitchurch", LocalDate.of(2023, 1, 1)
        ),
        TechnicianModel(
            70, "WD40","Larry",
            "074 9095 4037", "larry@gmail.com",
            "Crapstone, Devon", LocalDate.of(2023, 3, 1)
        )
    )

    fun addTechnician(
        code: String, name: String, phone: String, email: String, address: String, startDate: LocalDate
    ) {
        runningID += 1
        technicians.add(
            TechnicianModel(
                technicianID = runningID,
                technicianCode = code,
                name = name,
                phone = phone,
                email = email,
                address = address,
                startDate = startDate,
            )
        )
    }

    var bulls = mutableListOf<BullModel>(
        BullModel(7,"D7", "Bodacious", true),
        BullModel(8, "D8","Breaker-1-9", true),
        BullModel(9, "D9", "Good Ole Boy", false),
    )

    fun addBull(bullCode: String, bullName: String, sexedSemen: Boolean) {
        runningID += 1
        bulls.add(BullModel(
            bullID = runningID,
            bullCode = bullCode,
            bullName = bullName,
            sexedSemen = sexedSemen,
        ))
    }

    var farms = mutableListOf<FarmModel>(
        FarmModel(10, "OMCD123","Old MacDonald's", "NW5 3QL"),
        FarmModel(11, "OR456","O'Reilly's", "Table Flat Road, Apiti"),
    )

    fun addFarm(customerCode: String, name: String, address: String) {
        runningID += 1
        farms.add(FarmModel(
            farmID = runningID,
            customerCode = customerCode,
            name = name,
            address = address,

        ))
    }

    //    var currentFarm = mutableStateOf<String>("Null")
    var currentFarmID = 10
    var currentFarmName = ""

    fun selectFarm(farmID: Int) {
        currentFarmID = farmID
        currentFarmName = getCurrentFarm()?.name ?: ""
    }

    fun getCurrentFarm(): FarmModel? {
        return farms.find { it -> it.farmID == currentFarmID }
    }

    var cows = mutableListOf<CowModel>(
        CowModel(77,"Daisy", farmID = currentFarmID),
        CowModel(78, "Ayatollah", farmID = currentFarmID),
    )

    fun addCow(cowTagID: String): CowModel {
        runningID += 1
        val newCow = CowModel(cowID = runningID, cowTagID = cowTagID, farmID = currentFarmID)
        cows.add(newCow)
        return newCow
    }

    fun getCowsForCurrentFarm(): List<CowModel> {
        return cows.filter { it -> it.farmID == currentFarmID }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    var aiJobs = mutableListOf<AIJobModel>()

    var appTechnicianID = 69

    fun getAIJobsForCurrentFarm(): List<AIJobModel> {
        return aiJobs.filter { it -> it.farmID == currentFarmID }.reversed()
    }

    fun addAIJob(aiJobDate: LocalDate): Int {
        runningID += 1
        aiJobs.add(
            AIJobModel(
                aiJobID = runningID,
                farmID = currentFarmID,
                aiJobDate = aiJobDate,
                mainTechnicianID = appTechnicianID,
            )
        )
        return runningID
    }

    var currentAIJobID = 0
    var currentAIJobDate = LocalDate.of(1999, 12, 31)
    var currentAIJobDateString = ""

    fun setCurrentAIJob(aiJobID: Int) {
        currentAIJobID = aiJobID
        currentAIJobDate = getCurrentAIJob()?.aiJobDate ?: LocalDate.of(1999, 12, 31)
        currentAIJobDateString = getCurrentAIJob()?.aiJobDate.toString() ?: ""
    }

    fun getCurrentAIJob(): AIJobModel? {
        return aiJobs.find { it -> it.aiJobID == currentAIJobID  }
    }

    fun getTechnician(technicianID: Int): TechnicianModel? {
        return technicians.find { it -> it.technicianID == technicianID }
    }

    fun setMainTechnician(technicianID: Int) {
        val currentAIJob = getCurrentAIJob()
        if (currentAIJob != null) {
            currentAIJob.mainTechnicianID = technicianID
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    var proposedInseminations = mutableListOf<ProposedInseminationModel>()

    fun addProposedInsemination(bullID: Int, bullName: String, cowID: Int, cowTagID: String ) {

        val inseminationReturnInfo = getReturnInfoForCow(cowID = cowID, fromDate = currentAIJobDate)

        val currentAIJob = getCurrentAIJob()
        val mainTechnicianModel = currentAIJob?.let { getTechnician(technicianID = it.mainTechnicianID) }

        runningID += 1
        if (mainTechnicianModel != null) {
            proposedInseminations.add(
                ProposedInseminationModel(
                    proposedInseminationID = runningID,
                    farmID = currentFarmID,
                    bullID = bullID,
                    bullName = bullName,
                    cowID = cowID,
                    cowTagID = cowTagID,
                    technicianID = currentAIJob.mainTechnicianID,
                    technicianName = mainTechnicianModel.name,
                    aiJobID = currentAIJobID,
                    inseminationDate = currentAIJobDate,
                    inseminationReturnStatus = inseminationReturnInfo.inseminationReturnStatus,
                    daysSinceLastInsemination = inseminationReturnInfo.daysSinceLastInsemination,
                    lastInseminationBullName = inseminationReturnInfo.lastInseminationBullName,
                )
            )
        }
    }

    fun removeProposedInsemination(proposedInsemination: ProposedInseminationModel) {
        proposedInseminations.remove(proposedInsemination)
    }

    fun getProposedInseminationsForCurrentAIJob(): List<ProposedInseminationModel> {
        return proposedInseminations.filter { it -> it.aiJobID == currentAIJobID }
    }

    var inseminations = mutableListOf<InseminationModel>()

    fun addInseminationFromProposed(proposedInsemination: ProposedInseminationModel) {
        runningID += 1
        inseminations.add(
            InseminationModel(
                inseminationID = runningID,
                farmID = proposedInsemination.farmID,
                bullID = proposedInsemination.bullID,
                bullName = proposedInsemination.bullName,
                cowID = proposedInsemination.cowID,
                cowTagID = proposedInsemination.cowTagID,
                technicianID = proposedInsemination.technicianID,
                technicianName = proposedInsemination.technicianName,
                aiJobID = proposedInsemination.aiJobID,
                inseminationDate = proposedInsemination.inseminationDate,
                inseminationReturnStatus = proposedInsemination.inseminationReturnStatus,
                daysSinceLastInsemination = proposedInsemination.daysSinceLastInsemination,
                lastInseminationBullName = proposedInsemination.lastInseminationBullName,
            )
        )

        // Remove proposedInsemination
        proposedInseminations.remove(proposedInsemination)
    }

    fun getInseminationsForCurrentAIJob(): List<InseminationModel> {
        return inseminations.filter { it -> it.aiJobID == currentAIJobID }
    }

    fun getInseminationsForCow(cowID: Int): List<InseminationModel> {
        return inseminations.filter { it -> it.cowID == cowID }
    }

    fun getLastInseminationForCow(cowID: Int): InseminationModel? {
        val inseminationsForCow = getInseminationsForCow(cowID = cowID)

        if (inseminationsForCow.isEmpty()) {
            return null
        }

        else {
            val lastInsemination = inseminationsForCow.sortedBy { it.inseminationDate }.last()
            return lastInsemination
        }
    }

    fun getReturnInfoForCow(cowID: Int, fromDate: LocalDate): ReturnInfo {

        val lastInsemination = getLastInseminationForCow(cowID = cowID)

        if (lastInsemination == null) {
            return ReturnInfo(
                daysSinceLastInsemination = null,
                lastInseminationBullName = "",
                inseminationReturnStatus = InseminationReturnStatus.NEVER_INSEMINATED,
            )
        }

        val daysSinceLastInsemination = ChronoUnit.DAYS.between(
            lastInsemination.inseminationDate, fromDate
        ).toInt()

        val returnStatus = when (daysSinceLastInsemination) {
            in 0..17 -> InseminationReturnStatus.SHORT_RETURN
            in 18..24 -> InseminationReturnStatus.NORMAL_RETURN
            in 25..90 -> InseminationReturnStatus.LONG_RETURN
            else -> InseminationReturnStatus.INSEMINATED_AGES_AGO
        }

        return ReturnInfo(
            daysSinceLastInsemination = daysSinceLastInsemination,
            lastInseminationBullName = lastInsemination.bullName,
            inseminationReturnStatus = returnStatus,
        )
    }
}


data class ReturnInfo(
    val daysSinceLastInsemination: Int?,
    val lastInseminationBullName: String,
    val inseminationReturnStatus: InseminationReturnStatus,
)


enum class InseminationReturnStatus {
    NEVER_INSEMINATED, SHORT_RETURN, NORMAL_RETURN, LONG_RETURN, INSEMINATED_AGES_AGO
}


class InseminationModel(
    val inseminationID: Int,
    val farmID: Int,
    val bullID: Int,
    val bullName: String,
    val cowID: Int,
    val cowTagID: String,
    val technicianID: Int,
    val technicianName: String,
    val aiJobID: Int,
    val inseminationDate: LocalDate,
    val inseminationReturnStatus: InseminationReturnStatus,
    val daysSinceLastInsemination: Int?,
    val lastInseminationBullName: String,
) {}

class ProposedInseminationModel(
    val proposedInseminationID: Int,
    val farmID: Int,
    var bullID: Int,
    var bullName: String,
    val cowID: Int,
    val cowTagID: String,
    var technicianID: Int,
    var technicianName: String,
    val aiJobID: Int,
    val inseminationDate: LocalDate,
    val inseminationReturnStatus: InseminationReturnStatus,
    val daysSinceLastInsemination: Int?,
    val lastInseminationBullName: String,
) {}

class BullModel(
    val bullID: Int,
    val bullCode: String,
    val bullName: String,
    val sexedSemen: Boolean,
) {}

class CustomerModel(val customerCode: String, val customerName: String, )

class PersonModel(val name: String, val phoneNumber: Int, val email: String, )

class FarmModel(val farmID: Int, val customerCode: String, val name: String, val address: String) {}

class CowModel(val cowID: Int, val cowTagID: String, val farmID: Int) {}

class AIJobModel(
    val aiJobID: Int,
    val aiJobDate: LocalDate,
    val farmID: Int,
    var mainTechnicianID: Int
    ) {

    fun getTechnicianModel() {
        return
    }
}

class TechnicianModel(
    val technicianID: Int,
    val technicianCode: String,
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val startDate: LocalDate,
    ) {}
